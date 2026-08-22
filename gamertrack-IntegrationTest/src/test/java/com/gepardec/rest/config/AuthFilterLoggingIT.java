package com.gepardec.rest.config;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static io.restassured.RestAssured.with;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LAKWYC-9: authentication failures must be logged without the credential
 * material itself (Authorization header value, JWT token). These tests
 * capture everything the application logs below com.gepardec and sweep it
 * for token material.
 */
@QuarkusTest
public class AuthFilterLoggingIT {

    private static final Logger APP_LOGGER = Logger.getLogger("com.gepardec");
    private static final CapturingLogHandler capturedLogs = new CapturingLogHandler();

    @Inject
    JwtUtil jwtUtil;

    @ConfigProperty(name = "secret.default.pw")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "secret.admin.name")
    String SECRET_ADMIN_NAME;

    @BeforeAll
    public static void attachLogHandler() {
        APP_LOGGER.addHandler(capturedLogs);
    }

    @AfterAll
    public static void detachLogHandler() {
        APP_LOGGER.removeHandler(capturedLogs);
    }

    @BeforeEach
    public void setup() {
        RestAssured.basePath = "/gepardec-gamertrack/api/v1";
        capturedLogs.clear();
    }

    @Test
    public void malformedAuthorizationHeaderIsRejectedWithoutLoggingItsValue() {
        String headerValue = "Basic bWF4Om11c3RlcnBhc3N3b3Jk";

        requestUserCreation(headerValue)
                .then()
                .statusCode(401);

        String logText = capturedLogs.text();
        assertTrue(logText.contains("does not use the Bearer scheme"),
                "log should state the failure reason");
        assertFalse(logText.contains("bWF4Om11c3RlcnBhc3N3b3Jk"),
                "log must not contain the Authorization header value");
    }

    @Test
    public void missingAuthorizationHeaderIsRejectedWithReasonInLog() {
        with().when()
                .contentType("application/json")
                .body(new CreateUserCommand("max", "Muster"))
                .request("POST", "/users")
                .then()
                .statusCode(401);

        assertTrue(capturedLogs.text().contains("no Authorization header provided"),
                "log should state the failure reason");
    }

    @Test
    public void expiredTokenIsRejectedWithCategoryButWithoutToken() {
        String expiredToken = Jwts.builder()
                .subject(SECRET_ADMIN_NAME)
                .issuedAt(new Date(System.currentTimeMillis() - 20_000))
                .expiration(new Date(System.currentTimeMillis() - 10_000))
                .signWith(jwtUtil.generateKey())
                .compact();

        requestUserCreation("Bearer " + expiredToken)
                .then()
                .statusCode(401);

        String logText = capturedLogs.text();
        assertTrue(logText.contains("expired"),
                "log should carry the failure category");
        assertLogFreeOfTokenMaterial(logText, expiredToken);
    }

    @Test
    public void garbageTokenIsRejectedWithoutLoggingIt() {
        String garbageToken = "aksldfjalsdfjalskdjfaksdl.asdfasddfasdf.asdfsadff";

        requestUserCreation("Bearer " + garbageToken)
                .then()
                .statusCode(401);

        assertLogFreeOfTokenMaterial(capturedLogs.text(), garbageToken);
    }

    @Test
    public void successfulAuthenticationLogsNeitherTokenNorCredentials() {
        String token = with().when()
                .contentType("application/json")
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                .headers("Content-Type", ContentType.JSON,
                        "Accept", ContentType.JSON)
                .request("POST", "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .header("Authorization")
                .replace("Bearer ", "");

        String userToken = requestUserCreation("Bearer " + token)
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        with().headers("Authorization", "Bearer " + token)
                .when()
                .contentType("application/json")
                .pathParam("token", userToken)
                .request("DELETE", "/users/{token}");

        String logText = capturedLogs.text();
        assertTrue(logText.contains("Successfully authenticated user"));
        assertLogFreeOfTokenMaterial(logText, token);
        assertFalse(logText.contains(SECRET_DEFAULT_PW),
                "log must not contain the configured password");
    }

    private io.restassured.response.Response requestUserCreation(String authorizationHeader) {
        return with().when()
                .contentType("application/json")
                .body(new CreateUserCommand("max", "Muster"))
                .headers(
                        "Authorization",
                        authorizationHeader,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .request("POST", "/users");
    }

    /**
     * The full token must be absent, and so must its individual segments:
     * header and payload of an expired but otherwise valid token are
     * reusable material.
     */
    private void assertLogFreeOfTokenMaterial(String logText, String token) {
        for (String line : capturedLogs.records()) {
            assertFalse(line.contains(token),
                    "log must not contain the token, but this line does: " + line);
            for (String segment : token.split("\\.")) {
                assertFalse(line.contains(segment),
                        "log must not contain any token segment, but this line does: " + line);
            }
        }
    }

    /**
     * Captures raw log records (message, parameters and stack traces) below
     * the attached logger so tests can sweep them for credential material.
     */
    private static final class CapturingLogHandler extends Handler {

        private final List<String> lines = new CopyOnWriteArrayList<>();

        @Override
        public void publish(LogRecord record) {
            StringBuilder line = new StringBuilder();
            line.append('[').append(record.getLoggerName()).append("] ");
            if (record.getMessage() != null) {
                line.append(record.getMessage());
            }
            if (record.getParameters() != null) {
                for (Object parameter : record.getParameters()) {
                    line.append(' ').append(parameter);
                }
            }
            if (record.getThrown() != null) {
                StringWriter stackTrace = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(stackTrace));
                line.append(' ').append(stackTrace);
            }
            lines.add(line.toString());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        void clear() {
            lines.clear();
        }

        List<String> records() {
            return lines;
        }

        String text() {
            return String.join(System.lineSeparator(), lines);
        }
    }
}
