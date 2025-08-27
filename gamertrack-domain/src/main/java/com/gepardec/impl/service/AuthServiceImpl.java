package com.gepardec.impl.service;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.AuthService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.AuthCredential;
import com.gepardec.security.JwtUtil;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Transactional
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    static Dotenv dotenv = Dotenv.configure().directory("../../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));
    private static final String SECRET_ADMIN_NAME = dotenv.get("SECRET_ADMIN_NAME", System.getenv("SECRET_ADMIN_NAME"));

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Inject
    private AuthRepository authRepository;
    @Inject
    private JwtUtil jwtUtil;
    @Inject
    private TokenService tokenService;

    @Override
    public boolean authenticate(AuthCredential credential) {
        if (!(credential.getUsername().isBlank() || credential.getPassword().isBlank())) {
            Optional<AuthCredential> dbCredential = authRepository.findByUsername(credential.getUsername());

            if (dbCredential.isPresent()){
                if(jwtUtil.passwordsMatches(dbCredential.get().getPassword(), dbCredential.get().getSalt(), credential.getPassword())){
                    log.info("Credential {} authenticated", credential.getUsername());
                    return true;
                }
                log.error("Invalid credential: Credential dont match");
                return false;

            }
            log.error("Invalid credential: Credential not found");
            return false;

        }
        log.error("Invalid credential: Credential must be set");
        return false;

    }

    @Override
    public void createDefaultUser() {
        Map<String, String> credMap = jwtUtil.calcHashedCredentialMap(SECRET_DEFAULT_PW);

        AuthCredential credential = new AuthCredential(tokenService.generateToken(),SECRET_ADMIN_NAME,
                credMap.get("hashedPassword"), credMap.get("salt"));

        authRepository.deleteExistingAuthUsers();

        if (authRepository.createDefaultUser(credential).isPresent())
            log.info("Default user created");
    }

    @Override
    public boolean isTokenValid(String token) {
        boolean isValid = false;
        try {
            Jwts.parser().setSigningKey(jwtUtil.generateKey()).build().parseClaimsJws(token);
            isValid = true;
        } catch (JwtException e) {
            log.error("Token validation failed {}", e.getMessage());
        }
        return isValid;
    }
}
