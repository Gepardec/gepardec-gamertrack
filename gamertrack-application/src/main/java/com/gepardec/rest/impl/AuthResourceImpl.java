package com.gepardec.rest.impl;

import com.gepardec.core.services.AuthService;
import com.gepardec.core.services.LoginAttemptService;
import com.gepardec.rest.api.AuthResource;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.ValidateTokenCommand;
import com.gepardec.rest.model.mapper.AuthCredentialRestMapper;
import com.gepardec.security.JwtUtil;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@RequestScoped
@Transactional
public class AuthResourceImpl implements AuthResource {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final Logger log = LoggerFactory.getLogger(AuthResourceImpl.class);

    @Inject
    private AuthService authService;
    @Inject
    private AuthCredentialRestMapper mapper;
    @Inject
    private JwtUtil jwtUtil;
    @Inject
    private LoginAttemptService loginAttemptService;
    @Context
    HttpServerRequest request;


    @Override
    public Response login(AuthCredentialCommand authCredentialCommand) {
        String source = resolveSource();

        // Reject blocked sources before authenticating: the response stays identical for
        // right and wrong credentials, and no password hashing work is spent on them
        if (loginAttemptService.isBlocked(source)) {
            log.warn("Throttled login attempt from {} ({}: {})",
                    source, X_FORWARDED_FOR, request.getHeader(X_FORWARDED_FOR));
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Too many failed login attempts, try again later").build();
        }

        if (authService.authenticate(mapper.authCredentialCommandToAuthCredential(authCredentialCommand))) {
            loginAttemptService.loginSucceeded(source);

            String token = jwtUtil.generateToken(authCredentialCommand.username());

            return Response.ok("{\"token\": \"" + token + "\"}").header(AUTHORIZATION, "Bearer " + token).build();
        } else {
            loginAttemptService.loginFailed(source);
            log.warn("Failed login attempt from {} ({}: {})",
                    source, X_FORWARDED_FOR, request.getHeader(X_FORWARDED_FOR));
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }

    @Override
    public Response validateToken(ValidateTokenCommand tokenCmd) {
        if (tokenCmd.token() != null && authService.isTokenValid(tokenCmd.token())) return Response.ok().build();

        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
    }

    // The rightmost X-Forwarded-For entry is the one appended by our OpenShift router;
    // earlier entries are client-controlled and must not be trusted as the source
    private String resolveSource() {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] hops = forwardedFor.split(",");
            return hops[hops.length - 1].trim();
        }
        return request.remoteAddress() != null ? request.remoteAddress().hostAddress() : "unknown";
    }
}
