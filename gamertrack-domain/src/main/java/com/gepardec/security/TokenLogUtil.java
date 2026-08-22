package com.gepardec.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Helpers for logging authentication failures without writing credential
 * material (Authorization headers, JWTs, passwords, configured secrets)
 * into the log.
 */
public final class TokenLogUtil {

    private static final int FINGERPRINT_LENGTH = 8;

    private TokenLogUtil() {
    }

    /**
     * Maps a JWT parsing/validation exception to a short failure category
     * that is safe to log. Never returns token or claim contents.
     */
    public static String categorize(Exception e) {
        if (e instanceof ExpiredJwtException) {
            return "expired";
        }
        if (e instanceof SignatureException) {
            return "invalid signature";
        }
        if (e instanceof MalformedJwtException) {
            return "malformed";
        }
        if (e instanceof UnsupportedJwtException) {
            return "unsupported";
        }
        if (e instanceof IllegalArgumentException) {
            return "missing or blank token";
        }
        return e.getClass().getSimpleName();
    }

    /**
     * Short non-reversible fingerprint (first {@value FINGERPRINT_LENGTH}
     * hex chars of a SHA-256 hash) so log entries can be correlated to a
     * token without exposing it.
     */
    public static String fingerprint(String token) {
        if (token == null || token.isBlank()) {
            return "n/a";
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, FINGERPRINT_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            return "n/a";
        }
    }
}
