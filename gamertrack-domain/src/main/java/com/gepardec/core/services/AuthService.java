package com.gepardec.core.services;

import com.gepardec.model.AuthCredential;

public interface AuthService {
    boolean authenticate(AuthCredential credential);
    boolean createDefaultUserIfNotExists();
}
