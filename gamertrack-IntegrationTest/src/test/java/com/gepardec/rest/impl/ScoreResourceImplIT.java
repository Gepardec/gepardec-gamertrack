package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class ScoreResourceImplIT {

    private static String gameToken;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/gepardec-gamertrack/api/v1";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);


        gameToken = with()
                .when()
                .contentType("application/json")
                .body(new CreateGameCommand("Vier Gewinnt", "Nicht Schummeln"))
                .request("POST", "/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
    }

    @Test
    public void ensureCreateScoreWorks() {
        String userToken = with()
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
                .pathParam("token", gameToken)
                .request("GET", "/games/{token}")
                .then()
                .statusCode(200);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "/scores/")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "user.token", hasItem(equalTo(userToken)),
                        "game.token", hasItem(equalTo(gameToken))
                        );
    }

    @Test
    public void ensureGetScoreByTokenWorks() {
        String userToken = with()
                .when()
                .contentType("application/json")
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");



        String scoreToken =  with()
                .when()
                .contentType("application/json")
                .request("GET", "/scores/")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.user.token == '" + userToken + "' && it.game.token == '" + gameToken + "'}.token");

        with()
                .when()
                .contentType("application/json")
                .pathParam("token", scoreToken)
                .request("GET", "/scores/{token}")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "user.firstname", equalTo("Jakob"),
                        "user.lastname", equalTo("Muster"),
                        "user.deactivated", equalTo(false),
                        "user.token", equalTo(userToken),
                        "game.token", equalTo(gameToken),
                        "game.name", equalTo("Vier Gewinnt"),
                        "game.rules", equalTo("Nicht Schummeln"),
                        "score", equalTo(1500.0F)
                );
    }

    @Test
    public void ensureGetScoreByUserDoesNotFindDeletedUserEmptyList() {
        String userToken = with()
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
                .body(new UpdateUserCommand("Jakob", "Muster", true))
                .pathParam("userToken", userToken)
                .request("PUT", "/users/{userToken}")
                .then()
                .statusCode(200);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "/scores/?user=" + userToken +"&includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", equalTo(0));
    }

    @Test
    public void ensureGetScoreByScorePointsReturnsOk() {
        String userToken = with()
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
                .request("GET", "/scores/scorepoints/1500?includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "user.token", hasItem(equalTo(userToken)),
                        "game.token", hasItem(equalTo(gameToken))
                );
    }

    @Test
    public void ensureGetScoreByMinMaxReturnsOk() {
        String userToken = with()
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
                .request("GET", "/scores/?min=700&max=1600&includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "user.token", hasItem(equalTo(userToken)),
                        "game.token", hasItem(equalTo(gameToken))
                );
    }

    @Test
    public void ensureGetScoreByUserAndGameReturnsOk() {
        String userToken = with()
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
                .request("GET", "/scores/?user=" + userToken + "&game=" + gameToken)
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "[0].user.firstname", equalTo("Jakob"),
                        "[0].user.lastname", equalTo("Muster"),
                        "[0].user.deactivated", equalTo(false),
                        "[0].user.token", equalTo(userToken),
                        "[0].game.token", equalTo(gameToken),
                        "[0].game.name", equalTo("Vier Gewinnt"),
                        "[0].game.rules", equalTo("Nicht Schummeln"),
                        "[0].score", equalTo(1500.0f),
                        "size()", equalTo(1)
                );
    }

    @Test
    public void ensureGetScoreByMinReturnsEmptyLists() {
        String userToken = with()
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
                .body(new UpdateUserCommand("Jakob", "Muster", true))
                .pathParam("userToken", userToken)
                .request("PUT", "/users/{userToken}")
                .then()
                .statusCode(200);


        with()
                .when()
                .contentType("application/json")
                .request("GET", "/scores/?min=10200&includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "size()", equalTo(0)
                );
    }
}
