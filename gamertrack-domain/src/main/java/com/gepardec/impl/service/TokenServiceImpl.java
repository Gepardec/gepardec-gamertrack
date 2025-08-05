package com.gepardec.impl.service;

import com.gepardec.core.services.TokenService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;

@Transactional
@ApplicationScoped
public class TokenServiceImpl implements TokenService {
    @Override
    public String generateToken() {
        final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(BASE58_ALPHABET.length());
            token.append(BASE58_ALPHABET.charAt(index));
        }

        return token.toString();
    }
}