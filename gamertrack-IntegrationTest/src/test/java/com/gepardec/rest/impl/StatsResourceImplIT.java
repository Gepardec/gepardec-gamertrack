package com.gepardec.rest.impl;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.reset;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class StatsResourceImplIT {

    ArrayList<String> usesUserTokens = new ArrayList<>();
    ArrayList<String> usesGameTokens = new ArrayList<>();

    static String authHeader;
    String bearerToken;

    @ConfigProperty(name = "secret.default.pw")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "secret.admin.name")
    String SECRET_ADMIN_NAME;

    final String USER_PATH = "/users";
    final String GAME_PATH = "/games";
    final String MATCH_PATH = "/matches";
    final String STATS_PATH = "/stats";
    final String HEAD_TO_HEAD_PATH = STATS_PATH + "/head-to-head";

    @BeforeAll
    public static void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    @BeforeEach
    public void login() {
        basePath = "/gepardec-gamertrack/api/v1";
        if (authHeader == null) {
            authHeader = with().when()
                    .contentType("application/json")
                    .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                    .headers("Content-Type", ContentType.JSON,
                            "Accept", ContentType.JSON)
                    .request("POST", "/auth/login")
                    .then()
                    .statusCode(200)
                    .extract()
                    .header("Authorization");
        }
        bearerToken = authHeader.replace("Bearer ", "");
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
                    .request("DELETE", GAME_PATH + "/{token}");
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
                    .request("DELETE", USER_PATH + "/{token}");
        }
        usesUserTokens.clear();
    }

    @AfterAll
    public static void cleanup() {
        reset();
    }

    @Test
    void ensureStatsForKnownMatchHistoryReturnExactNumbers() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();
        UserRestDto opponent = createUser();

        for (int i = 0; i < 6; i++) {
            createMatch(game, player, opponent);
        }
        for (int i = 0; i < 3; i++) {
            createMatch(game, opponent, player);
        }

        var statsJson = authorized()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("userToken", equalTo(player.token()))
                .body("gameToken", equalTo(game.token()))
                .body("matchesPlayed", equalTo(9))
                .body("wins", equalTo(6))
                .body("draws", equalTo(0))
                .body("losses", equalTo(3))
                .body("currentStreak.type", equalTo("LOSS"))
                .body("currentStreak.length", equalTo(3))
                .body("longestWinStreak", equalTo(6))
                .body("excludedMatches", equalTo(0))
                .extract()
                .jsonPath();

        // JSON numbers are parsed as float by RestAssured, so compare with float precision
        assertEquals(6.0 / 9.0, statsJson.getDouble("winRate"), 1e-6);
    }

    @Test
    void ensureWinningThreeInARowThenLosingOnceUpdatesStreaks() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();
        UserRestDto opponent = createUser();

        for (int i = 0; i < 3; i++) {
            createMatch(game, player, opponent);
        }

        authorized()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("currentStreak.type", equalTo("WIN"))
                .body("currentStreak.length", equalTo(3))
                .body("longestWinStreak", equalTo(3));

        createMatch(game, opponent, player);

        authorized()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("currentStreak.type", equalTo("LOSS"))
                .body("currentStreak.length", equalTo(1))
                .body("longestWinStreak", equalTo(3));
    }

    @Test
    void ensureFormReturnsRequestedNumberOfNewestResultsFirst() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();
        UserRestDto opponent = createUser();

        for (int i = 0; i < 5; i++) {
            createMatch(game, player, opponent);
        }
        for (int i = 0; i < 3; i++) {
            createMatch(game, opponent, player);
        }

        authorized()
                .queryParam("limit", 5)
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}/form")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("results", contains("LOSS", "LOSS", "LOSS", "WIN", "WIN"))
                .body("excludedMatches", equalTo(0));
    }

    @Test
    void ensureFormWithLimitLargerThanMatchCountReturnsAllResults() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();
        UserRestDto opponent = createUser();

        for (int i = 0; i < 4; i++) {
            createMatch(game, player, opponent);
        }

        authorized()
                .queryParam("limit", 50)
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}/form")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("results", hasSize(4));
    }

    @Test
    void ensureFormWithZeroOrNegativeLimitReturnsBadRequest() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();

        for (int limit : new int[]{0, -5}) {
            authorized()
                    .queryParam("limit", limit)
                    .pathParam("userToken", player.token())
                    .pathParam("gameToken", game.token())
                    .get(STATS_PATH + "/players/{userToken}/games/{gameToken}/form")
                    .then()
                    .statusCode(Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Test
    void ensureHeadToHeadReturnsMutualRecordAndMirroredResultForSwappedUsers() {
        GameRestDto game = createGame();
        UserRestDto playerA = createUser();
        UserRestDto playerB = createUser();

        for (int i = 0; i < 7; i++) {
            createMatch(game, playerA, playerB);
        }
        for (int i = 0; i < 3; i++) {
            createMatch(game, playerB, playerA);
        }
        // a match against someone else must not influence the mutual record
        createMatch(game, playerA, createUser());

        authorized()
                .queryParam("firstUserToken", playerA.token())
                .queryParam("secondUserToken", playerB.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("matchesPlayed", equalTo(10))
                .body("firstUserWins", equalTo(7))
                .body("secondUserWins", equalTo(3))
                .body("draws", equalTo(0));

        authorized()
                .queryParam("firstUserToken", playerB.token())
                .queryParam("secondUserToken", playerA.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("matchesPlayed", equalTo(10))
                .body("firstUserWins", equalTo(3))
                .body("secondUserWins", equalTo(7))
                .body("draws", equalTo(0));
    }

    @Test
    void ensureHeadToHeadUsesPlacementComparisonInMultiplayerMatches() {
        GameRestDto game = createGame();
        UserRestDto playerA = createUser();
        UserRestDto playerB = createUser();

        // four player match: A places 2nd, B places 4th -> counts as win for A
        createMatch(game, createUser(), playerA, createUser(), playerB);

        authorized()
                .queryParam("firstUserToken", playerA.token())
                .queryParam("secondUserToken", playerB.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("matchesPlayed", equalTo(1))
                .body("firstUserWins", equalTo(1))
                .body("secondUserWins", equalTo(0))
                .body("draws", equalTo(0));
    }

    @Test
    void ensurePlayerWhoNeverPlayedTheGameGetsZeroedStats() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();

        authorized()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("matchesPlayed", equalTo(0))
                .body("wins", equalTo(0))
                .body("draws", equalTo(0))
                .body("losses", equalTo(0))
                .body("winRate", equalTo(0.0f))
                .body("currentStreak.type", equalTo("NONE"))
                .body("currentStreak.length", equalTo(0))
                .body("longestWinStreak", equalTo(0));
    }

    @Test
    void ensurePlayersWhoNeverMetGetEmptyHeadToHead() {
        GameRestDto game = createGame();
        UserRestDto playerA = createUser();
        UserRestDto playerB = createUser();

        createMatch(game, playerA, createUser());

        authorized()
                .queryParam("firstUserToken", playerA.token())
                .queryParam("secondUserToken", playerB.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("matchesPlayed", equalTo(0))
                .body("firstUserWins", equalTo(0))
                .body("secondUserWins", equalTo(0))
                .body("draws", equalTo(0));
    }

    @Test
    void ensureUnknownUserOrGameTokensReturnNotFound() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();

        authorized()
                .pathParam("userToken", "unknownUserToken")
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());

        authorized()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", "unknownGameToken")
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());

        authorized()
                .queryParam("firstUserToken", player.token())
                .queryParam("secondUserToken", "unknownUserToken")
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureHeadToHeadWithMissingOrEqualUserTokensReturnsBadRequest() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();

        authorized()
                .queryParam("firstUserToken", player.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        authorized()
                .queryParam("firstUserToken", player.token())
                .queryParam("secondUserToken", player.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureStatsEndpointsAreSecuredAndRejectUnauthenticatedRequests() {
        GameRestDto game = createGame();
        UserRestDto player = createUser();
        UserRestDto opponent = createUser();

        given()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}")
                .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());

        given()
                .pathParam("userToken", player.token())
                .pathParam("gameToken", game.token())
                .get(STATS_PATH + "/players/{userToken}/games/{gameToken}/form")
                .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());

        given()
                .queryParam("firstUserToken", player.token())
                .queryParam("secondUserToken", opponent.token())
                .queryParam("gameToken", game.token())
                .get(HEAD_TO_HEAD_PATH)
                .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());
    }

    private io.restassured.specification.RequestSpecification authorized() {
        return given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Accept",
                        ContentType.JSON)
                .when();
    }

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

    public MatchRestDto createMatch(GameRestDto gameRestDto, UserRestDto... usersInPlacementOrder) {
        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                Arrays.stream(usersInPlacementOrder)
                        .map(user -> new User(null, user.firstname(), user.lastname(),
                                user.deactivated(), user.token()))
                        .toList());

        return with()
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
    }
}
