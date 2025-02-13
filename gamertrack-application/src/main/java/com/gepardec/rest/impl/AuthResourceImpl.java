package com.gepardec.rest.impl;

import com.gepardec.core.services.AuthService;
import com.gepardec.rest.api.AuthResource;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.mapper.AuthCredentialsRestMapper;
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
    private AuthCredentialsRestMapper mapper;
    @Inject
    private JwtUtil jwtUtil;


    @Override
    public Response login(AuthCredentialCommand authCredentialCommand) {
        authService.createDefaultUserIfNotExists();
        if (authService.authenticate(mapper.AuthCredentialCommandToAuthCredential(authCredentialCommand))) {

            String token = jwtUtil.generateToken(authCredentialCommand.username());

            return Response.ok("{\"token\": \"" + token + "\"}").header(AUTHORIZATION, "Bearer " + token).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }
}
