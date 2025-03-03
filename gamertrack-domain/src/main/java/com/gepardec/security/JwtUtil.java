package com.gepardec.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.lang.codec.Hex;
import org.apache.shiro.lang.util.ByteSource;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class JwtUtil {
    static Dotenv dotenv = Dotenv.configure().directory("../../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_KEY = dotenv.get("SECRET_JWT_HASH", System.getenv("SECRET_JWT_HASH"));

    public String generateToken(String username) {
        long start = System.nanoTime();
        var jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS512, generateKey())
                .compact();

        long end = System.nanoTime();
        System.out.println("Time to generate token: " + (end - start) / 1000000 + "ms");
        return jwt;
    }

    public boolean passwordsMatches(String dbHashedPassword, String saltText, String clearTextPassword) {
        long start = System.nanoTime();
        ByteSource salt = ByteSource.Util.bytes(Hex.decode(saltText));
        String hashedPassword = hashAndSaltPassword(clearTextPassword, salt);
        long end = System.nanoTime();
        System.out.println("Time to hash and salt password: " + (end - start) / 1000000 + "ms");
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
        long start = System.nanoTime();
        var temp = new SecretKeySpec(SECRET_KEY.getBytes(), 0, SECRET_KEY.getBytes().length, "HmacSHA512");
        long end = System.nanoTime();
        System.out.println("Time to generate key: " + (end - start) / 1000000 + "ms");
        return temp;
    }
}
