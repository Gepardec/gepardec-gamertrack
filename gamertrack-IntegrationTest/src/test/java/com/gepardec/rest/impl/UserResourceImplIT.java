package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.github.cdimascio.dotenv.Dotenv;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class UserResourceImplIT {

        static List<String> usedUserTokens = new ArrayList<>();

        static String authHeader;
        String bearerToken = authHeader.replace("Bearer ", "");

        static Dotenv dotenv = Dotenv.configure().directory("../").filename("secret.env").ignoreIfMissing().load();
        private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));
        private static final String SECRET_ADMIN_NAME = dotenv.get("SECRET_ADMIN_NAME", System.getenv("SECRET_ADMIN_NAME"));


        @BeforeAll
        public static void setup() {
                RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
                enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
                authHeader = with().when()
                        .contentType("application/json")
                        .body(new AuthCredentialCommand(SECRET_ADMIN_NAME,SECRET_DEFAULT_PW))
                        .headers("Content-Type", ContentType.JSON,
                                "Accept", ContentType.JSON)
                        .request("POST", "/auth/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .header("Authorization");
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
        public void ensureCreateUserWorks() {
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
        public void ensureUpdateUserWorks() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Jakob", "Muster"))
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
                        .extract()
                        .path("token");
                usedUserTokens.add(token);

                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Paul", "Mustermann", false))
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with()
                        .when()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .contentType("application/json")
                        .pathParam("token", token)
                        .request("GET", "/users/{token}")
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .body(
                                "firstname", equalTo("Paul"),
                                "lastname", equalTo("Mustermann")
                        );
        }

        @Test
        public void ensureDeleteUserWorks() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Max", "Muster"))
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
                        .extract()
                        .path("token");

                with()
                        .when()
                        .contentType("application/json")
                        .pathParam("token", token)
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .request("DELETE", "/users/{token}")
                        .then()
                        .statusCode(200);
        }

        @Test
        public void ensureDeleteNotExistingUserReturnsNotFound() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Max", "Muster"))
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
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .request("DELETE", "/users/sdfk23s3df36sa")
                        .then()
                        .statusCode(404);
        }

        @Test
        public void ensureGetUserReturnsOk() {
                with().when()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .request("GET", "/users")
                        .then()
                        .statusCode(200);
        }

        @Test
        public void ensureGetUserByTokenReturnsOk() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Max", "Muster"))
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
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .pathParam("token", token)
                        .request("GET", "/users/{token}")
                        .then()
                        .statusCode(200)
                        .body(
                                "firstname", equalTo("Max"),
                                "lastname", equalTo("Muster")
                        );
        }

        @Test
        public void ensureGetDeletedUserReturnsNotFound() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Max", "Muster"))
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
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Max", "Muster", true))
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with().when()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .request("GET", "/users?includeDeactivated=false")
                        .then()
                        .body("token", not(hasItem(token)))
                        .statusCode(200);
        }

        @Test
        public void ensureGetDeletedUserIncludesDeletedReturnsOk() {
                String token = with()
                        .when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("Max", "Muster"))
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
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Max", "Muster", true))
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with().when()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .request("GET", "/users?includeDeactivated=true")
                        .then()
                        .body("token", hasItem(token))
                        .statusCode(200);
        }
}