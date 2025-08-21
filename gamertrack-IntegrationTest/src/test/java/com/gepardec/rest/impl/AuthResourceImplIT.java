package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.ValidateTokenCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class AuthResourceImplIT {
    static List<String> usedUserTokens = new ArrayList<>();
    String bearerToken;

    @ConfigProperty(name = "SECRET_DEFAULT_PW")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "SECRET_ADMIN_NAME")
    String SECRET_ADMIN_NAME;

    @BeforeAll
    public static void setup() {
        //RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
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
                    .request("DELETE", "api/v1/users/{token}")
            ;
        }
    }

    @Test
    public void createTestUserWithoutAuthHeader() {
        with().when()
                .contentType("application/json")
                .body(new CreateUserCommand("max","Muster"))
                .request("POST", "api/v1/users")
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
                .request("POST", "api/v1/auth/login")
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
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .assertThat()
                .body("firstname", equalTo("max"))
                .extract()
                .path("token");
        usedUserTokens.add(token);
    }

    @Test
    public void ensureValidateTokenForInvalidTokenReturnsUnauthorized() {
        ValidateTokenCommand validateTokenCommand = new ValidateTokenCommand("aksldfjalsdfjalskdjfaksdl.asdfasddfasdf.asdfsadff");
        with().when()
                .contentType("application/json")
                .body(validateTokenCommand)
                .post("api/v1/auth/validate")
                .then()
                .statusCode(401);
    }

    @Test
    public void ensureValidateTokenForNotProvidedOrNullTokenReturnsUnauthorized() {
        ValidateTokenCommand validateTokenCommand = new ValidateTokenCommand(null);
        with().when()
                .contentType("application/json")
                .body(validateTokenCommand)
                .post("api/v1/auth/validate")
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
                .request("POST", "api/v1/auth/login")
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
                .post("api/v1/auth/validate")
                .then()
                .statusCode(200);
    }
}
