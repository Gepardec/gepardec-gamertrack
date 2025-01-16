package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNot.not;

public class UserResourceImplIT {

        static List<String> usedUserTokens = new ArrayList<>();

        @BeforeAll
        public static void setup() {
                RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
                enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        }

        @AfterEach
        public void after() {
                for (String token : usedUserTokens) {
                        with()
                                .when()
                                .contentType("application/json")
                                .pathParam("token", token)
                                .request("DELETE", "/users/{token}");
                }
        }

        @Test
        public void ensureCreateUserWorks() {
                String token = with().when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("max","Muster"))
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
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with()
                        .when()
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
                        .request("POST", "/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("token");

                with()
                        .when()
                        .contentType("application/json")
                        .pathParam("token", token)
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
                        .request("POST", "/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
                        .request("DELETE", "/users/sdfk23s3df36sa")
                        .then()
                        .statusCode(404);
        }

        @Test
        public void ensureGetUserReturnsOk() {
                with().when()
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
                        .request("POST", "/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("token");
                usedUserTokens.add(token);


                with()
                        .when()
                        .contentType("application/json")
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
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with().when()
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
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200);

                with().when()
                        .request("GET", "/users?includeDeactivated=true")
                        .then()
                        .body("token", hasItem(token))
                        .statusCode(200);
        }
}