package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.ValidateTokenCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class AuthResourceImplIT {
    static List<String> usedUserTokens = new ArrayList<>();
    String bearerToken;

    @ConfigProperty(name = "secret.default.pw")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "secret.admin.name")
    String SECRET_ADMIN_NAME;


    @BeforeAll
    public static void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    @BeforeEach
    public void setBasePath() {
        RestAssured.basePath = "/gepardec-gamertrack/api/v1";
    }

    @AfterEach
    public void tearDown() {
        for (String token : usedUserTokens) {
            with()
                    .headers(
                            "Authorization",
                            "Bearer " + bearerToken,
                            "Content-Type",
                            ContentType.JSON,
                            "Accept",
                            ContentType.JSON)
                    .when()
                    .contentType("application/json")
                    .pathParam("token", token)
                    .request("DELETE", "/users/{token}")
            ;
        }
    }

    @Test
    public void createTestUserWithoutAuthHeader() {
        with().when()
                .contentType("application/json")
                .body(new CreateUserCommand("max","Muster"))
                .request("POST", "/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void createTestUserWithAuthHeader() {
        String authHeader = with().when()
                .contentType("application/json")
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME,SECRET_DEFAULT_PW))
                .headers("Content-Type", ContentType.JSON,
                        "Accept", ContentType.JSON)
                .request("POST", "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .header("Authorization");

        bearerToken = authHeader.replace("Bearer ", "");

        String token = with().when()
                .contentType("application/json")
                .body(new CreateUserCommand("max","Muster"))
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .request("POST", "/users")
                .then()
                .statusCode(201)
                .assertThat()
                .body("firstname", equalTo("max"))
                .extract()
                .path("token");
        usedUserTokens.add(token);
    }

    @Test
    public void ensureRepeatedFailedLoginsAreThrottledUniformlyAndRecoverAfterLockout() throws InterruptedException {
        // TEST-NET source address so the lockout does not affect the other tests,
        // which are throttled by their real remote address
        String throttledSource = "203.0.113.7";

        // login.throttle.max-failures=3 (see src/test/resources/application.properties)
        for (int i = 0; i < 3; i++) {
            with().when()
                    .contentType("application/json")
                    .header("X-Forwarded-For", throttledSource)
                    .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, "definitely-wrong-password"))
                    .request("POST", "/auth/login")
                    .then()
                    .statusCode(401);
        }

        // While blocked, wrong and correct credentials get the identical response
        String throttledBodyWrongPw = with().when()
                .contentType("application/json")
                .header("X-Forwarded-For", throttledSource)
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, "definitely-wrong-password"))
                .request("POST", "/auth/login")
                .then()
                .statusCode(429)
                .extract()
                .asString();

        String throttledBodyCorrectPw = with().when()
                .contentType("application/json")
                .header("X-Forwarded-For", throttledSource)
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                .request("POST", "/auth/login")
                .then()
                .statusCode(429)
                .extract()
                .asString();

        assertEquals(throttledBodyWrongPw, throttledBodyCorrectPw);

        // After the lockout window (login.throttle.lockout-seconds=3) a correct login succeeds again
        Thread.sleep(3500);

        with().when()
                .contentType("application/json")
                .header("X-Forwarded-For", throttledSource)
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                .request("POST", "/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    public void ensureValidateTokenForInvalidTokenReturnsUnauthorized() {
        ValidateTokenCommand validateTokenCommand = new ValidateTokenCommand("aksldfjalsdfjalskdjfaksdl.asdfasddfasdf.asdfsadff");
        with().when()
                .contentType("application/json")
                .body(validateTokenCommand)
                .post("/auth/validate")
                .then()
                .statusCode(401);
    }

    @Test
    public void ensureValidateTokenForNotProvidedOrNullTokenReturnsUnauthorized() {
        ValidateTokenCommand validateTokenCommand = new ValidateTokenCommand(null);
        with().when()
                .contentType("application/json")
                .body(validateTokenCommand)
                .post("/auth/validate")
                .then()
                .statusCode(401);
    }

    @Test
    public void ensureValidateTokenForValidTokenReturns200Ok() {
        //Login to get valid token
        String authHeader = with().when()
                .contentType("application/json")
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                .headers("Content-Type", ContentType.JSON,
                        "Accept", ContentType.JSON)
                .request("POST", "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .header("Authorization");

        var token = authHeader.replace("Bearer ", "");


        //Validate token
        ValidateTokenCommand validateTokenCommand = new ValidateTokenCommand(token);
        with().when()
                .contentType("application/json")
                .body(validateTokenCommand)
                .post("/auth/validate")
                .then()
                .statusCode(200);
    }
}
