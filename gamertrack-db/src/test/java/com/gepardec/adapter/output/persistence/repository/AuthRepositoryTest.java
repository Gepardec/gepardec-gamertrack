package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.AuthCredential;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class AuthRepositoryTest  {
    @Inject
    EntityManager entityManager;

    @Inject
    AuthRepository authRepository;
    @Inject
    TokenService tokenService;


    @Test
    void ensureCreateDefaultUserWorks() {
        AuthCredential authCredential = new AuthCredential(tokenService.generateToken(), "admin",
                "password", "salt");

        authRepository.createDefaultUser(authCredential);

        assertTrue(authRepository.findByUsername(authCredential.getUsername()).isPresent());
        assertEquals("admin", authRepository.findByUsername(authCredential.getUsername()).get().getUsername());
    }

    @Test
    void ensureCreateDefaultUserWorksIfUserAlreadyExists() {
        //GIVEN
        AuthCredential existingAuthCredential = new AuthCredential(tokenService.generateToken(), "oldAdmin",
                "password", "salt");
        authRepository.createDefaultUser(existingAuthCredential);


        //WHEN
        AuthCredential newAuthCredential = new AuthCredential(tokenService.generateToken(), "newAdmin",
                "password", "salt");


        //THEN

        assertTrue(authRepository.findByUsername(existingAuthCredential.getUsername()).isPresent());
        assertFalse(authRepository.findByUsername(newAuthCredential.getUsername()).isPresent());
        }


    @Test
    void ensureFindByUsernameReturnsEmpty() {
        AuthCredential authCredential = new AuthCredential(tokenService.generateToken(), "WrongName",
                "password", "salt");
        assertTrue(authRepository.findByUsername(authCredential.getUsername()).isEmpty());
    }

    @Test
    void ensureFindByUsernameReturnsUser() {
        AuthCredential authCredential = new AuthCredential(tokenService.generateToken(), "CorrectName",
                "password", "salt");

        authRepository.createDefaultUser(authCredential);

        assertEquals("CorrectName", authRepository.findByUsername(authCredential.getUsername()).get().getUsername());
    }
}
