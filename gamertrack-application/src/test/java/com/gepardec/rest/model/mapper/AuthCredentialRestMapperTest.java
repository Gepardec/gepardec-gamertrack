package com.gepardec.rest.model.mapper;

import com.gepardec.model.AuthCredential;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthCredentialRestMapperTest {
    @InjectMocks
    AuthCredentialRestMapper mapper;

    @Test
    public void ensureMapAuthCredentialCommandToAuthCredentialWorks() {
        AuthCredentialCommand authCredentialCommand = new AuthCredentialCommand("username", "password");

        AuthCredential mappedCredential = mapper.authCredentialCommandToAuthCredential(authCredentialCommand);

        assertEquals(authCredentialCommand.username(), mappedCredential.getUsername());
        assertEquals(authCredentialCommand.password(), mappedCredential.getPassword());
    }
}
