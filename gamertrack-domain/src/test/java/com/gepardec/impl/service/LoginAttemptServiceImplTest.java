package com.gepardec.impl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginAttemptServiceImplTest {

    static final String SOURCE = "203.0.113.7";

    LoginAttemptServiceImpl loginAttemptService;
    long now;

    @BeforeEach
    void setup() {
        loginAttemptService = new LoginAttemptServiceImpl();
        loginAttemptService.maxFailures = 3;
        loginAttemptService.windowSeconds = 60;
        loginAttemptService.lockoutSeconds = 300;
        loginAttemptService.currentTimeMillis = () -> now;
        now = 0;
    }

    @Test
    void ensureSourceIsNotBlockedWithoutFailures() {
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureSourceIsNotBlockedBelowMaxFailures() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureSourceIsBlockedAfterMaxFailuresWithinWindow() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        assertTrue(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureFailuresOutsideWindowDoNotCountTowardsBlocking() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        now = 61_000;
        loginAttemptService.loginFailed(SOURCE);
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureBlockExpiresAfterLockout() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);

        now = 299_999;
        assertTrue(loginAttemptService.isBlocked(SOURCE));
        now = 300_000;
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureFailureCountRestartsAfterLockoutExpired() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);

        now = 300_000;
        loginAttemptService.loginFailed(SOURCE);
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureSuccessfulLoginResetsFailures() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginSucceeded(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        assertFalse(loginAttemptService.isBlocked(SOURCE));
    }

    @Test
    void ensureSourcesAreBlockedIndependently() {
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        loginAttemptService.loginFailed(SOURCE);
        assertTrue(loginAttemptService.isBlocked(SOURCE));
        assertFalse(loginAttemptService.isBlocked("198.51.100.1"));
    }
}
