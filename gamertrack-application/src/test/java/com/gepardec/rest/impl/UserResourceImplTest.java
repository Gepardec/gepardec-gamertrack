package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNot.not;

public class UserResourceImplTest {

        @BeforeAll
        public static void setup() {
                RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
        }

        @Test
        public void ensureCreateUserWorks() {
                with().when()
                        .contentType("application/json")
                        .body(new CreateUserCommand("max","Muster"))
                        .request("POST", "/users")
                        .then()
                        .statusCode(201)
                        .assertThat()
                        .body("firstname", equalTo("max"))
                        .log().body();
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

                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Paul", "Mustermann", false))
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200)
                        .log().body();

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
                        )
                        .log().body();
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
                        .statusCode(200)
                        .log().body();
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

                with()
                        .when()
                        .contentType("application/json")
                        .request("DELETE", "/users/sdfk23s3df36sa")
                        .then()
                        .statusCode(404)
                        .log().body();
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
                        )
                        .log().body();
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

                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Max", "Muster", true))
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200)
                        .log().body();

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

                with()
                        .when()
                        .contentType("application/json")
                        .body(new UpdateUserCommand("Max", "Muster", true))
                        .pathParam("token", token)
                        .request("PUT", "/users/{token}")
                        .then()
                        .statusCode(200)
                        .log().body();

                with().when()
                        .request("GET", "/users?includeDeactivated=true")
                        .then()
                        .body("token", hasItem(token))
                        .statusCode(200);
        }
}