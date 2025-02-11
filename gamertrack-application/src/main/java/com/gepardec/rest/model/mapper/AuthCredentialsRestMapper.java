package com.gepardec.rest.model.mapper;

import com.gepardec.model.AuthCredential;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthCredentialsRestMapper {
    public AuthCredential AuthCredentialCommandToAuthCredential(AuthCredentialCommand authCredentialCommand) {
        return new AuthCredential(authCredentialCommand.username(), authCredentialCommand.password() );
    }
}
