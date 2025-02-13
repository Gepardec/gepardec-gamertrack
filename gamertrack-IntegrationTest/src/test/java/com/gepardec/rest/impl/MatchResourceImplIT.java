package com.gepardec.rest.impl;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.*;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class MatchResourceImplIT {

    ArrayList<String> usesMatchTokens = new ArrayList<>();
    ArrayList<String> usesUserTokens = new ArrayList<>();
    ArrayList<String> usesGameTokens = new ArrayList<>();

    static String authHeader;
    String bearerToken = authHeader.replace("Bearer ", "");

    static Dotenv dotenv = Dotenv.configure().directory("../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));

    final String USER_PATH = "/users";
    final String GAME_PATH = "/games";
    final String MATCH_PATH = "/matches";

    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        authHeader = with().when()
                .contentType("application/json")
                .body(new AuthCredentialCommand("admin",SECRET_DEFAULT_PW))
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
        for (String token : usesGameTokens) {
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
                    .request("DELETE", "/games/{token}");
        }
        usesGameTokens.clear();
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
                    .request("DELETE", "/users/{token}");
        }
        usesUserTokens.clear();
    }

    @AfterAll
    public static void cleanup() {
        reset();
    }

    @Test
    void ensureGetMatchesReturnsForExistingMatches200OkWithMatchesList() {
        MatchRestDto createdMatch = createMatch();

        var foundMatches =
                when()
                        .get(MATCH_PATH)
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .extract()
                        .jsonPath()
                        .getList(".", MatchRestDto.class);

        foundMatches.getFirst().equals(createdMatch);
        usesMatchTokens.add(createdMatch.token());
    }

    @Test
    void ensureGetMatchesWithGameTokenAndWithoutUserTokenReturnsMatchReferencingTheSameGame() {
        GameRestDto gameThatShouldntBeFound = with()
                .body(new CreateGameCommand("gameThatShouldntBeFound", "no rules"))
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .contentType("application/json")
                .accept("application/json")
                .when()
                .post("/games")
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .body()
                .as(GameRestDto.class);

        usesGameTokens.add(gameThatShouldntBeFound.token());

        GameRestDto createdGame = createGame();


        MatchRestDto matchThatShouldNotBeFound = createMatch(createUser(),createUser(), gameThatShouldntBeFound);
        MatchRestDto matchToBeFound1 = createMatch(createUser(),createUser(), createdGame);
        MatchRestDto matchToBeFound2 = createMatch(createUser(),createUser(), createdGame);

        var foundMatches =
                given()
                        .queryParam("gameToken", createdGame.token())
                        .when()
                        .get(MATCH_PATH)
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .extract()
                        .jsonPath()
                        .getList("", MatchRestDto.class);

        assertTrue(foundMatches.stream().map(MatchRestDto::token).toList()
                .containsAll(List.of(matchToBeFound1.token(), matchToBeFound2.token())));
        assertFalse(foundMatches.stream()
                .anyMatch(match -> match.token().equals(matchThatShouldNotBeFound.token())));
    }

    @Test
    void ensureGetMatchesWithoutGameTokenAndWithUserTokenReturnsMatchReferencingTheSameUser() {
        UserRestDto createdUser = createUser();
        GameRestDto createdGame = createGame();
        MatchRestDto matchThatShouldNotBeFound = createMatch(createUser(),createUser(), createdGame);
        MatchRestDto matchThatShouldBeFound1 = createMatch(createdUser,createUser(), createdGame);
        MatchRestDto matchThatShouldBeFound2 = createMatch(createdUser,createUser(), createdGame);

        var foundMatches =
                given()
                        .queryParam("userToken", createdUser.token())
                        .when()
                        .get(MATCH_PATH)
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .body("", hasSize(2))
                        .extract()
                        .jsonPath()
                        .getList("", MatchRestDto.class);

        assertTrue(
                foundMatches.stream().map(MatchRestDto::token).toList()
                        .containsAll(
                                List.of(matchThatShouldBeFound1.token(), matchThatShouldBeFound2.token())));
        assertFalse(foundMatches.stream()
                .anyMatch(match -> match.token().equals(matchThatShouldNotBeFound.token())));
    }

    @Test
    void ensureGetMatchesWithGameTokenAndUserTokenReturnsMatchReferencingTheSameGameAndUser() {
        UserRestDto createdUser = createUser();
        GameRestDto createdGame = createGame();

        GameRestDto gameThatShouldntBeFound = with()
                .body(new CreateGameCommand("gameThatShouldntBeFound", "no rules"))
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .contentType("application/json")
                .accept("application/json")
                .when()
                .post(GAME_PATH)
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .body()
                .as(GameRestDto.class);

        usesGameTokens.add(gameThatShouldntBeFound.token());

        MatchRestDto matchThatShouldNotBeFound = createMatch(createdUser,createUser(), gameThatShouldntBeFound);
        MatchRestDto matchThatShouldNotBeFound2 = createMatch(createUser(),createUser(), gameThatShouldntBeFound);
        MatchRestDto matchThatShouldBeFound1 = createMatch(createdUser,createUser(), createdGame);
        MatchRestDto matchThatShouldBeFound2 = createMatch(createdUser,createUser(), createdGame);

        var foundMatches = given().queryParam("gameToken", createdGame.token())
                .queryParam("userToken", createdUser.token())
                .when()
                .get(MATCH_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("", MatchRestDto.class);

        assertTrue(foundMatches.stream()
                .allMatch(match -> match.game().token().equals(createdGame.token())
                        && match.users().stream().map(UserRestDto::token).toList().contains(createdUser.token())));
        assertFalse(
                foundMatches.containsAll(List.of(matchThatShouldNotBeFound2, matchThatShouldNotBeFound)));
        assertTrue(foundMatches.containsAll(List.of(matchThatShouldBeFound1, matchThatShouldBeFound2)));
    }

    @Test
    void ensureGetMatchByTokenForExistingMatchReturnsMatch() {
        MatchRestDto existingMatch = createMatch();

        given()
                .pathParam("token", existingMatch.token())
                .when()
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", equalTo(existingMatch.token()));
    }

    @Test
    void ensureGetMatchByTokenForNonExistingMatchReturnsNotFound() {

        given()
                .pathParam("token", "alkjsflaksjdf")
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureCreateMatchForValidMatchReturns200OkWithNewMatch() {
        GameRestDto gameRestDto = createGame();
        UserRestDto userRestDto1 = createUser();
        UserRestDto userRestDto2 = createUser();


        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                List.of(new User(userRestDto1.id(), userRestDto1.firstname(), userRestDto1.lastname(),
                                userRestDto1.deactivated(), userRestDto1.token()),
                        new User(userRestDto2.id(), userRestDto2.firstname(), userRestDto2.lastname(),
                                userRestDto2.deactivated(), userRestDto2.token())));

        MatchRestDto createdMatch =
                with()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(createMatchCommand)
                        .contentType("application/json")
                        .post(MATCH_PATH)
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .body("token", notNullValue())
                        .extract()
                        .body()
                        .as(MatchRestDto.class);

        assertEquals(createdMatch.game().token(), createMatchCommand.game().getToken());
        assertTrue(createdMatch.users().containsAll(createMatchCommand.users().stream().map(UserRestDto::new).toList()));
        usesMatchTokens.add(createdMatch.token());
    }

    @Test
    void ensureCreateMatchForInvalidMatchReturns400BadRequest() {
        UserRestDto userRestDto = createUser();

        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, null, "anything", "should fail"),
                List.of(new User(null, userRestDto.firstname(), userRestDto.lastname(),
                        userRestDto.deactivated(), userRestDto.token())));

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(createMatchCommand)
                .contentType("application/json")
                .post(MATCH_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureUpdateMatchForExistingMatchReturns200OkWithUpdatedMatch() {
        MatchRestDto existingMatch = createMatch();
        UserRestDto userRestDto = createUser();
        UpdateMatchCommand matchToUpdate = new UpdateMatchCommand(
                new Game(null, existingMatch.game().token(),
                        existingMatch.game().name(),
                        existingMatch.game().rules()),
                List.of(
                        existingMatch.users().stream().map(urd -> new User(urd.id(), urd.firstname(), urd.lastname(), urd.deactivated(), urd.token()))
                                .findFirst().get(),
                        new User(null, userRestDto.firstname(), userRestDto.lastname(),
                                userRestDto.deactivated(), userRestDto.token()),
                        new User(null, userRestDto.firstname(), userRestDto.lastname(),
                                userRestDto.deactivated(), userRestDto.token())));

        var updatedMatch =
                given()

                        .pathParam("token", existingMatch.token())
                        .contentType("application/json")
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(matchToUpdate)
                        .put("%s/{token}".formatted(MATCH_PATH))
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .body("token", equalTo(existingMatch.token()))
                        .body("users", hasSize(matchToUpdate.users().size()))
                        .extract()
                        .as(MatchRestDto.class);

        assertEquals(updatedMatch.token(), existingMatch.token());
        assertNotEquals(matchToUpdate.users().size(), existingMatch.users().size());
    }

    @Test
    void ensureUpdateMatchForNonExistingMatchReturns400BadRequest() {
        UpdateMatchCommand matchToUpdate = RestTestFixtures.updateMatchCommand();

        given()
                .pathParam("token", "12k31k2j3ksadj")
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(matchToUpdate)
                .put("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureDeleteMatchForExistingMatchReturns200OkWithDeletedMatch() {
        MatchRestDto existingMatch = createMatch();

        MatchRestDto deletedMatch =
                given()
                        .pathParam("token", existingMatch.token())
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .delete("%s/{token}".formatted(MATCH_PATH))
                        .then()
                        .statusCode(Status.OK.getStatusCode())
                        .extract()
                        .as(MatchRestDto.class);

        assertEquals(existingMatch.token(), deletedMatch.token());
    }

    @Test
    void ensureDeleteMatchForNonExistingMatchReturns404NotFound() {
        given()
                .pathParam("token", "12k31k2j3ksadj")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .delete("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    //-------------------HELPER METHODS -------------------------//
    public UserRestDto createUser() {
        UserRestDto userRestDto =
                with()
                        .contentType("application/json")
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(new CreateUserCommand("max", "Muster"))
                        .post(USER_PATH)
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract()
                        .body()
                        .as(UserRestDto.class);

        usesUserTokens.add(userRestDto.token());
        return userRestDto;
    }
    public GameRestDto createGame() {
        GameRestDto gameRestDto = with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(new CreateGameCommand("default Game", "no rules"))
                .contentType("application/json")
                .accept("application/json")
                .when()
                .post(GAME_PATH)
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .body()
                .as(GameRestDto.class);

        usesGameTokens.add(gameRestDto.token());
        return gameRestDto;
    }

    public MatchRestDto createMatch() {
        return createMatch(createUser(),createUser(), createGame());
    }

    public MatchRestDto createMatch(UserRestDto userRestDto1,UserRestDto userRestDto2, GameRestDto gameRestDto) {
        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                List.of(new User(null, userRestDto1.firstname(), userRestDto1.lastname(),
                                userRestDto1.deactivated(), userRestDto1.token()),
                        new User(null, userRestDto2.firstname(), userRestDto2.lastname(),
                                userRestDto2.deactivated(), userRestDto2.token())));

        MatchRestDto createdMatch =
                with()
                        .headers(
                                "Authorization",
                                "Bearer " + bearerToken,
                                "Content-Type",
                                ContentType.JSON,
                                "Accept",
                                ContentType.JSON)
                        .body(createMatchCommand)
                        .contentType("application/json")
                        .post(MATCH_PATH)
                        .then()
                        .statusCode(Status.CREATED.getStatusCode())
                        .extract()
                        .body()
                        .as(MatchRestDto.class);

        usesMatchTokens.add(createdMatch.token());

        return createdMatch;
    }


}
