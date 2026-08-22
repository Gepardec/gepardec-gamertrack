package com.gepardec.core.services;

public interface LoginAttemptService {

    boolean isBlocked(String source);

    void loginFailed(String source);

    void loginSucceeded(String source);
}
