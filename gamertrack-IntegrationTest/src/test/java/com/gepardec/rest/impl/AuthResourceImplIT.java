package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;

public class AuthResourceImplIT {
    static List<String> usedUserTokens = new ArrayList<>();
    String bearerToken;

    static Dotenv dotenv = Dotenv.configure().directory("../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));
    private static final String SECRET_ADMIN_NAME = dotenv.get("SECRET_ADMIN_NAME", System.getenv("SECRET_ADMIN_NAME"));


    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
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
}
