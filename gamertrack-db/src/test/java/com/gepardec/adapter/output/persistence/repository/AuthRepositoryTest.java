package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.AuthCredential;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(ArquillianExtension.class)
public class AuthRepositoryTest extends GamertrackDbIT {
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    AuthRepository authRepository;
    @Inject
    TokenService tokenService;


    @Test
    void ensureCreateDefaultUserWorks() {
        AuthCredential authCredential = new AuthCredential(tokenService.generateToken(), "admin",
                "password", "salt");
        assertTrue(authRepository.createDefaultUserIfNotExists(authCredential));
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

        authRepository.createDefaultUserIfNotExists(authCredential);

        assertEquals("CorrectName", authRepository.findByUsername(authCredential.getUsername()).get().getUsername());
    }
}
