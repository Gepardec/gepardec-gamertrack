package com.gepardec.rest.config;

import com.gepardec.security.JwtUtil;
import com.gepardec.security.TokenLogUtil;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Key;
import java.security.Principal;

@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String BEARER = "Bearer";

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {


        String authHeader = reqCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            // Never log the header value itself, it may contain a valid credential
            log.info("Authorization rejected: {}", authHeader == null
                    ? "no Authorization header provided"
                    : "Authorization header does not use the Bearer scheme");

            throw new NotAuthorizedException("No authorization header provided");
        }

        String token = authHeader.substring(BEARER.length()).trim();

        try {
            Key key = jwtUtil.generateKey();
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            SecurityContext securityContext = reqCtx.getSecurityContext();
            reqCtx.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
                }

                @Override
                public boolean isUserInRole(String s) {
                    return securityContext.isUserInRole(s);
                }

                @Override
                public boolean isSecure() {
                    return securityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return securityContext.getAuthenticationScheme();
                }
            });
            log.info("Successfully authenticated user");

        } catch (Exception e) {
            // Log only the failure category and a non-reversible fingerprint,
            // never the token itself
            log.error("Invalid JWT token ({}), fingerprint {}",
                    TokenLogUtil.categorize(e), TokenLogUtil.fingerprint(token));
            reqCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
