package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class GameResourceImplIT {

    ArrayList<String> usesTokens = new ArrayList<>();

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
    public void after() {
        for (String token : usesTokens) {
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
                    .request("DELETE", "api/v1/games/{token}")
            ;
        }
    }

    @AfterAll
    public static void cleanup() {
        reset();
    }


    @Test
    void ensureGetGamesWithExistingGameReturns200OkWithListContainingGame() {
        //GIVEN
        GameRestDto existingGame = createGame();
        //WHEN THEN

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

        var foundGames =
                when()
                        .get("api/v1/games")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("", GameRestDto.class);

        assertTrue(foundGames.contains(existingGame));
    }

    @Test
    void ensureGetGameWithExistingGameReturnsGame() {
        GameRestDto existingGame = createGame();

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

        var foundGame =
                when()
                        .get("api/v1/games/%s".formatted(existingGame.token()))
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .extract()
                        .body()
                        .as(GameRestDto.class);

        assertEquals(foundGame, existingGame);
        usesTokens.add(existingGame.token());
    }

    @Test
    void ensureGetGameWithNoExistingGameReturns404() {

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

        when()
                .get("api/v1/games/askjfaskl1230qqis")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureCreateGameReturnsCreatedGameForValidGame() {
        CreateGameCommand gameToBeCreated = RestTestFixtures.createGameCommand();

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

        var responeObjectFromRequest =
                with()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(gameToBeCreated)
                        .contentType("application/json")
                        .when()
                        .post("api/v1/games")
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .body("token", notNullValue())
                        .body("name", samePropertyValuesAs(gameToBeCreated.name()))
                        .body("rules", samePropertyValuesAs(gameToBeCreated.rules()));

        usesTokens.add(responeObjectFromRequest.extract().jsonPath().get("token"));
    }
/*
    @Test
    void ensureCreateGameWithInvalidGameReturns400BadRequest() {

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

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("", ""))
                .contentType("application/json")
                .post("api/v1/games")
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }


 */
    @Test
    void ensureUpdateGameReturnsForValidGameUpdatedGame() {

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

        GameRestDto existingGame = createGame();

        UpdateGameCommand gameToBeUpdated = new UpdateGameCommand("UpatedTestGame", "still no rules");

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(gameToBeUpdated)
                .contentType("application/json")
                .put("api/v1/games/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", samePropertyValuesAs(existingGame.token()))
                .body("name", samePropertyValuesAs(gameToBeUpdated.name()))
                .body("rules", samePropertyValuesAs(gameToBeUpdated.rules()));

        usesTokens.add(existingGame.token());
    }

    @Test
    void ensureUpdateGameWithNotExistingGameReturns404NotFound() {
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

        UpdateGameCommand gameToBeUpdated = new UpdateGameCommand("UpatedTestGame", "still no rules");

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(gameToBeUpdated)
                .contentType("application/json")
                .put("api/v1/games/asdaskjfalsfj")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }


    @Test
    void ensureDeleteExistingGameReturns200OkWithDeletedGame() {

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

        GameRestDto existingGame = createGame();

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .delete("api/v1/games/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", samePropertyValuesAs(existingGame.token()))
                .body("name", samePropertyValuesAs(existingGame.name()))
                .body("rules", samePropertyValuesAs(existingGame.rules()));
    }

    //--------------Helper Methods------------------------

    public GameRestDto createGame() {

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

        GameRestDto gameRestDto =
                with()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(new CreateGameCommand("news game", "no rules"))
                        .contentType("application/json")
                        .accept("application/json")
                        .when()
                        .post("api/v1/games")
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract()
                        .body()
                        .as(GameRestDto.class);

        usesTokens.add(gameRestDto.token());
        return gameRestDto;
    }

}