package com.gepardec.core.repository;

import com.gepardec.model.AuthCredential;

import java.util.Optional;


public interface AuthRepository {
    Optional<AuthCredential> findByUsername(String username);
    boolean createDefaultUserIfNotExists(AuthCredential authCredential);
}
