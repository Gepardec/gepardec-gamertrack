package com.gepardec.rest.impl;

import com.gepardec.core.services.AuthService;
import com.gepardec.rest.api.AuthResource;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.ValidateTokenCommand;
import com.gepardec.rest.model.mapper.AuthCredentialRestMapper;
import com.gepardec.security.JwtUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

@RequestScoped
@Transactional
public class AuthResourceImpl implements AuthResource {

    @Inject
    private AuthService authService;
    @Inject
    private AuthCredentialRestMapper mapper;
    @Inject
    private JwtUtil jwtUtil;


    @Override
    public Response login(AuthCredentialCommand authCredentialCommand) {
        long start = System.nanoTime();
        if (authService.authenticate(mapper.authCredentialCommandToAuthCredential(authCredentialCommand))) {

            String token = jwtUtil.generateToken(authCredentialCommand.username());

            long end = System.nanoTime();
            System.out.println("Time until response is started to be built: " + (end - start) / 1000000 + "ms");
            return Response.ok("{\"token\": \"" + token + "\"}").header(AUTHORIZATION, "Bearer " + token).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }

    @Override
    public Response validateToken(ValidateTokenCommand tokenCmd) {
        if (tokenCmd.token() != null && authService.isTokenValid(tokenCmd.token())) return Response.ok().build();

        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
    }
}
