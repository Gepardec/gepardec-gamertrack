package com.gepardec.impl.service;

import com.gepardec.core.repository.AuthRepository;
import com.gepardec.core.services.AuthService;
import com.gepardec.model.AuthCredential;
import com.gepardec.security.JwtUtil;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Transactional
@Stateless
public class AuthServiceImpl implements AuthService {

    static Dotenv dotenv = Dotenv.configure().directory("../../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW");

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Inject
    private AuthRepository authRepository;

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public boolean authenticate(AuthCredential credential) {
        if (!credential.getUsername().isBlank() || credential.getPassword().isBlank()) {
            Optional<AuthCredential> dbCredential = authRepository.findByUsername(credential);
            log.info("Found credential {}", credential.getUsername());
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
    public void createDefaultUserIfNotExists() {
        Map<String, String> credMap = jwtUtil.hashPassword(SECRET_DEFAULT_PW);

        AuthCredential credential = new AuthCredential();
        credential.setUsername("admin");
        credential.setPassword(credMap.get("hashedPassword"));
        credential.setSalt(credMap.get("salt"));

        Optional<AuthCredential> dbAuthCredentialEntity = authRepository.findByUsername(credential);

        if (!dbAuthCredentialEntity.isPresent()) {
            authRepository.createDefaultUserIfNotExists(credential);
            credMap = null;
            log.info("Default user created");
        }
        else{
            log.info("Default user exists");
        }
    }
}
