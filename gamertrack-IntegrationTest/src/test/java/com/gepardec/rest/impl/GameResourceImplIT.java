package com.gepardec.rest.impl;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameResourceImplIT {

    ArrayList<String> usesTokens = new ArrayList<>();

    static String authHeader;
    String bearerToken = authHeader.replace("Bearer ", "");

    static Dotenv dotenv = Dotenv.configure().directory("../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));
    private static final String SECRET_ADMIN_NAME = dotenv.get("SECRET_ADMIN_NAME", System.getenv("SECRET_ADMIN_NAME"));



    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1";
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
                    .request("DELETE", "/games/{token}")
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
        var foundGames =
                when()
                        .get("/games")
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
                        .get("/games/%s".formatted(existingGame.token()))
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
                .get("/games/askjfaskl1230qqis")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureCreateGameReturnsCreatedGameForValidGame() {
        CreateGameCommand gameToBeCreated = RestTestFixtures.createGameCommand();

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
                        .post("/games")
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
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("", "invalid game"))
                .contentType("application/json")
                .post("/games")
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureUpdateGameReturnsForValidGameUpdatedGame() {
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
                .put("/games/%s".formatted(existingGame.token()))
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
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(gameToBeUpdated)
                .contentType("application/json")
                .put("/games/asdaskjfalsfj")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }


    @Test
    void ensureDeleteExistingGameReturns200OkWithDeletedGame() {
        GameRestDto existingGame = createGame();

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .delete("/games/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", samePropertyValuesAs(existingGame.token()))
                .body("name", samePropertyValuesAs(existingGame.name()))
                .body("rules", samePropertyValuesAs(existingGame.rules()));
    }

    //--------------Helper Methods------------------------

    public GameRestDto createGame() {
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
                        .post("/games")
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract()
                        .body()
                        .as(GameRestDto.class);

        usesTokens.add(gameRestDto.token());
        return gameRestDto;
    }

}