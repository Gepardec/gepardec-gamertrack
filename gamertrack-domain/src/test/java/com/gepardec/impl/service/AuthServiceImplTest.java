package com.gepardec.impl.service;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.AuthCredential;
import com.gepardec.security.JwtUtil;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    AuthRepository authRepository;
    @InjectMocks
    AuthServiceImpl authService;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    TokenService tokenService;

    @Test
    void ensureCreateDefaultUserIfNotExistsCreatesDefaultUser() {
        when(authRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(authRepository.createDefaultUserIfNotExists(any())).thenReturn(true);

        assertEquals(authService.createDefaultUserIfNotExists(),true);
    }

    @Test
    void ensureCreateDefaultUserIfNotExistsWorksReturnsFalse() {
        when(authRepository.findByUsername(any())).thenReturn(Optional.of(new AuthCredential()));

        assertEquals(authService.createDefaultUserIfNotExists(),false);
    }

    @Test
    void ensureAuthenticateReturnsFalseIfCredentialsAreBlank() {
        assertEquals(authService.authenticate(new AuthCredential("admin", "")), false);
    }

    @Test
    void ensureAuthenticateReturnsFalseIfUsernameDoesNotExist() {
        when(authRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertEquals(authService.authenticate(new AuthCredential("admin", "test")), false);
    }

    @Test
    void ensureAuthenticateReturnsFalseWhenCredentialsAreWrong() {
        when(authRepository.findByUsername(any())).thenReturn(Optional.of(new AuthCredential("admin", "WrongPW")));
        assertEquals(authService.authenticate(new AuthCredential("admin", "WrongPW")), false);
    }

    @Test
    void ensureAuthenticateReturnsTrueWhenCredentialsCorrect() {
        when(authRepository.findByUsername(any())).thenReturn(Optional.of(new AuthCredential("admin", "CorrectPW")));
        when(jwtUtil.passwordsMatches("CorrectPW",null,"CorrectPW")).thenReturn(true);
        assertEquals(authService.authenticate(new AuthCredential("admin", "CorrectPW")), true);
    }

}
