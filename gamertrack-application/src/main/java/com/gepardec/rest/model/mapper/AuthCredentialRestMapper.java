package com.gepardec.rest.model.mapper;

import com.gepardec.model.AuthCredential;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthCredentialRestMapper {
    public AuthCredential authCredentialCommandToAuthCredential(AuthCredentialCommand authCredentialCommand) {
        return new AuthCredential(authCredentialCommand.username(), authCredentialCommand.password() );
    }
}
