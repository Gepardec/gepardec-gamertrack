package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
public class ScoreResourceImplIT {

    private static String gameToken;
    ArrayList<String> usesUserTokens = new ArrayList<>();



    String bearerToken;

    @ConfigProperty(name = "SECRET_DEFAULT_PW")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "SECRET_ADMIN_NAME")
    String SECRET_ADMIN_NAME;

    @BeforeEach
    public void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

    }

    @AfterEach
    public void cleanup() {
        for (String token : usesUserTokens) {
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
                    .request("DELETE", "api/v1/users/{token}");
        }
        usesUserTokens.clear();
    }

    @Test
    public void ensureCreateScoreWorks() {
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt1", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

        with()
                .when()
                .contentType("application/json")
                .pathParam("token", gameToken)
                .request("GET", "api/v1/games/{token}")
                .then()
                .statusCode(200);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/")
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt2", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);



        String scoreToken =  with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.user.token == '" + userToken + "' && it.game.token == '" + gameToken + "'}.token");

        with()
                .when()
                .contentType("application/json")
                .pathParam("token", scoreToken)
                .request("GET", "api/v1/scores/{token}")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "user.firstname", equalTo("Jakob"),
                        "user.lastname", equalTo("Muster"),
                        "user.deactivated", equalTo(false),
                        "user.token", equalTo(userToken),
                        "game.token", equalTo(gameToken),
                        "game.name", equalTo("Vier Gewinnt2"),
                        "game.rules", equalTo("Nicht Schummeln"),
                        "score", equalTo(1500.0F)
                );
    }

    @Test
    public void ensureGetScoreByUserDoesNotFindDeletedUserEmptyList() {
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt3", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
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
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

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
                .body(new UpdateUserCommand("Jakob", "Muster", true))
                .pathParam("userToken", userToken)
                .request("PUT", "api/v1/users/{userToken}")
                .then()
                .statusCode(200);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?user=" + userToken +"&includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", equalTo(0));
    }

    @Test
    public void ensureGetScoreByScorePointsReturnsOk() {
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt4", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/scorepoints/1500?includeDeactivated=false")
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt5", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?min=700&max=1600&includeDeactivated=false")
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt6", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?user=" + userToken + "&game=" + gameToken)
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "[0].user.firstname", equalTo("Jakob"),
                        "[0].user.lastname", equalTo("Muster"),
                        "[0].user.deactivated", equalTo(false),
                        "[0].user.token", equalTo(userToken),
                        "[0].game.token", equalTo(gameToken),
                        "[0].game.name", equalTo("Vier Gewinnt6"),
                        "[0].game.rules", equalTo("Nicht Schummeln"),
                        "[0].score", equalTo(1500.0f),
                        "size()", equalTo(1)
                );
    }

    @Test
    public void ensureGetScoreByMinReturnsEmptyLists() {
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

        gameToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + authHeader.replace("Bearer ", ""),
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("Vier Gewinnt7", "Nicht Schummeln"))
                .request("POST", "api/v1/games")
                .then()
                .statusCode(201)
                .extract()
                .path("token");

        String userToken = with()
                .when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateUserCommand("Jakob", "Muster"))
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("token");
        usesUserTokens.add(userToken);

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
                .body(new UpdateUserCommand("Jakob", "Muster", true))
                .pathParam("userToken", userToken)
                .request("PUT", "api/v1/users/{userToken}")
                .then()
                .statusCode(200);

        with()
                .when()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?min=10200&includeDeactivated=false")
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        "size()", equalTo(0)
                );
    }
}
