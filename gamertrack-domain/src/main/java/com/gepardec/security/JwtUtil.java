package com.gepardec.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.lang.codec.Hex;
import org.apache.shiro.lang.util.ByteSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class JwtUtil {
    @ConfigProperty(name = "secret.jwt.hash")
    String SECRET_KEY;

    @ConfigProperty(name = "jwt.expiration.minutes", defaultValue = "60")
    long JWT_EXPIRATION_MINUTES;

    public String generateToken(String username) {
        var jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * JWT_EXPIRATION_MINUTES))
                .signWith(SignatureAlgorithm.HS512, generateKey())
                .compact();

        return jwt;
    }

    public boolean passwordsMatches(String dbHashedPassword, String saltText, String clearTextPassword) {
        ByteSource salt = ByteSource.Util.bytes(Hex.decode(saltText));
        String hashedPassword = hashAndSaltPassword(clearTextPassword, salt);
        return hashedPassword.equals(dbHashedPassword);
    }

    public Map<String, String> calcHashedCredentialMap(String clearTextPassword) {
        ByteSource salt = getSalt();

        Map<String, String> credMap = new HashMap<>();
        credMap.put("hashedPassword", hashAndSaltPassword(clearTextPassword, salt));
        credMap.put("salt", salt.toHex());
        return credMap;

    }

    private String hashAndSaltPassword(String clearTextPassword, ByteSource salt) {
        return new Sha512Hash(clearTextPassword, salt, 210000).toHex();
    }

    private ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }

    public Key generateKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), 0, SECRET_KEY.getBytes().length, "HmacSHA512");
    }
}
