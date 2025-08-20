package com.gepardec.impl.service;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.AuthCredential;
import com.gepardec.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @Test
    void ensureIsTokenValidReturnsFalseIfTokenIsNull() {
        assertFalse(authService.isTokenValid(null));
    }

    @Test
    void ensureIsTokenValidReturnsFalseIfTokenIsInvalid() {
        assertFalse(authService.isTokenValid("invalidToken.shouldNotWork.shouldBeFalse"));
    }

    @Test
    void ensureIsTokenValidReturnsTrueIfTokenIsValid() {
        when(jwtUtil.generateToken(any())).thenCallRealMethod();
        when(jwtUtil.generateKey()).thenCallRealMethod();


        assertTrue(authService.isTokenValid(jwtUtil.generateToken("AnyUser")));
    }
}
