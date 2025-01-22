package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.restassured.filter.log.LogDetail;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameResourceImplIT {

    ArrayList<String> usesTokens = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1/games";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    @AfterEach
    public void after() {
        for (String token : usesTokens) {
            delete("/%s".formatted(token));
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
        var foundGames =
                when()
                        .get()
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

        var foundGame =
                when()
                        .get("/%s".formatted(existingGame.token()))
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
        when()
                .get("askjfaskl1230qqis")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureCreateGameReturnsCreatedGameForValidGame() {
        CreateGameCommand gameToBeCreated = RestTestFixtures.createGameCommand();

        var responeObjectFromRequest =
                with()
                        .body(gameToBeCreated)
                        .contentType("application/json")
                        .when()
                        .post()
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .body("token", notNullValue())
                        .body("name", samePropertyValuesAs(gameToBeCreated.name()))
                        .body("rules", samePropertyValuesAs(gameToBeCreated.rules()));

        usesTokens.add(responeObjectFromRequest.extract().jsonPath().get("token"));
    }

    @Test
    void ensureCreateGameWithInvalidGameReturns400BadRequest() {

        with()
                .body(new CreateGameCommand("", "invalid game"))
                .contentType("application/json")
                .post()
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureUpdateGameReturnsForValidGameUpdatedGame() {
        GameRestDto existingGame = createGame();

        UpdateGameCommand gameToBeUpdated = new UpdateGameCommand("UpatedTestGame", "still no rules");

        with()
                .body(gameToBeUpdated)
                .contentType("application/json")
                .put("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", samePropertyValuesAs(existingGame.token()))
                .body("name", samePropertyValuesAs(gameToBeUpdated.name()))
                .body("rules", samePropertyValuesAs(gameToBeUpdated.rules()));

        usesTokens.add(existingGame.token());
    }

    @Test
    void ensureUpdateGameWithNotExistingGameReturns404NotFound() {

        UpdateGameCommand gameToBeUpdated = new UpdateGameCommand("UpatedTestGame", "still no rules");

        with()
                .body(gameToBeUpdated)
                .contentType("application/json")
                .put("/asdaskjfalsfj")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    /*
    @Test
    void ensureDeleteExistingGameReturns200OkWithDeletedGame() {
        GameRestDto existingGame = createGame();

        with()
                .delete("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", samePropertyValuesAs(existingGame.token()))
                .body("name", samePropertyValuesAs(existingGame.name()))
                .body("rules", samePropertyValuesAs(existingGame.rules()));
    }
    /*
     */

    //--------------Helper Methods------------------------

    public GameRestDto createGame() {
        GameRestDto gameRestDto =
                with()
                        .body(new CreateGameCommand("news game", "no rules"))
                        .contentType("application/json")
                        .accept("application/json")
                        .when()
                        .post()
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract()
                        .body()
                        .as(GameRestDto.class);

        usesTokens.add(gameRestDto.token());
        return gameRestDto;
    }

}