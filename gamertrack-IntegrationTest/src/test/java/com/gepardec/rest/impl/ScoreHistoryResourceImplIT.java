package com.gepardec.rest.impl;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.ScoreHistoryRestDto;
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
import java.util.List;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.reset;
import static io.restassured.RestAssured.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ScoreHistoryResourceImplIT {

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
    final String SCOREHISTORY_PATH = "/scorehistory";

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
    void ensureCreateMatchPersistsOneScoreHistoryEntryPerParticipant() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        MatchRestDto match = createMatch(winner, loser, game);

        List<ScoreHistoryRestDto> historyEntries = getScoreHistory(
                "?match=" + match.token());

        assertEquals(2, historyEntries.size());

        ScoreHistoryRestDto winnerEntry = historyEntries.stream()
                .filter(entry -> entry.user().token().equals(winner.token())).findFirst().get();
        ScoreHistoryRestDto loserEntry = historyEntries.stream()
                .filter(entry -> entry.user().token().equals(loser.token())).findFirst().get();

        assertEquals(game.token(), winnerEntry.game().token());
        assertEquals(match.token(), winnerEntry.matchToken());
        assertEquals(1500.0, winnerEntry.previousScorePoints());
        assertEquals(1516.0, winnerEntry.newScorePoints());
        assertEquals(16.0, winnerEntry.scoreChange());

        assertEquals(game.token(), loserEntry.game().token());
        assertEquals(match.token(), loserEntry.matchToken());
        assertEquals(1500.0, loserEntry.previousScorePoints());
        assertEquals(1484.0, loserEntry.newScorePoints());
        assertEquals(-16.0, loserEntry.scoreChange());
    }

    @Test
    void ensureCreateMatchPersistsScoreHistoryMatchingCurrentScore() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        MatchRestDto match = createMatch(winner, loser, game);

        ScoreHistoryRestDto winnerEntry = getScoreHistory(
                "?user=" + winner.token() + "&game=" + game.token()).getFirst();

        float currentScorePoints = with()
                .when()
                .contentType("application/json")
                .request("GET", "/scores/?user=" + winner.token() + "&game=" + game.token())
                .then()
                .statusCode(200)
                .extract()
                .path("[0].score");

        assertEquals(match.token(), winnerEntry.matchToken());
        assertEquals(currentScorePoints, (float) winnerEntry.newScorePoints());
    }

    @Test
    void ensureSecondMatchAppendsScoreHistoryEntryWithPreviousScoreOfPriorEntry() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        createMatch(winner, loser, game);
        createMatch(winner, loser, game);

        List<ScoreHistoryRestDto> winnerEntries = getScoreHistory(
                "?user=" + winner.token() + "&game=" + game.token());

        assertEquals(2, winnerEntries.size());
        assertEquals(winnerEntries.get(0).newScorePoints(),
                winnerEntries.get(1).previousScorePoints());
        assertEquals(winnerEntries.get(1).newScorePoints() - winnerEntries.get(1)
                .previousScorePoints(), winnerEntries.get(1).scoreChange());
        assertTrue(winnerEntries.get(1).scoreChange() > 0);
    }

    @Test
    void ensureGetScoreHistoryByTokenReturnsScoreHistoryEntry() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        MatchRestDto match = createMatch(winner, loser, game);

        ScoreHistoryRestDto historyEntry = getScoreHistory("?match=" + match.token()).getFirst();

        with()
                .when()
                .contentType("application/json")
                .pathParam("token", historyEntry.token())
                .request("GET", SCOREHISTORY_PATH + "/{token}")
                .then()
                .statusCode(200)
                .assertThat()
                .body("token", org.hamcrest.Matchers.equalTo(historyEntry.token()),
                        "matchToken", org.hamcrest.Matchers.equalTo(match.token()));
    }

    @Test
    void ensureScoreHistoryEntriesCannotBeModifiedOrDeletedViaApi() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        MatchRestDto match = createMatch(winner, loser, game);

        ScoreHistoryRestDto historyEntry = getScoreHistory("?match=" + match.token()).getFirst();

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(historyEntry)
                .pathParam("token", historyEntry.token())
                .put(SCOREHISTORY_PATH + "/{token}")
                .then()
                .statusCode(Status.METHOD_NOT_ALLOWED.getStatusCode());

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .pathParam("token", historyEntry.token())
                .delete(SCOREHISTORY_PATH + "/{token}")
                .then()
                .statusCode(Status.METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Test
    void ensureScoreHistoryEntriesAreRetainedAfterMatchDeletion() {
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        GameRestDto game = createGame();

        MatchRestDto match = createMatch(winner, loser, game);

        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .pathParam("token", match.token())
                .delete(MATCH_PATH + "/{token}")
                .then()
                .statusCode(Status.OK.getStatusCode());

        List<ScoreHistoryRestDto> historyEntries = getScoreHistory("?match=" + match.token());

        assertEquals(2, historyEntries.size());
    }

    //-------------------HELPER METHODS -------------------------//
    public List<ScoreHistoryRestDto> getScoreHistory(String queryString) {
        return with()
                .when()
                .contentType("application/json")
                .request("GET", SCOREHISTORY_PATH + queryString)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", ScoreHistoryRestDto.class);
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

    public MatchRestDto createMatch(UserRestDto userRestDto1, UserRestDto userRestDto2,
            GameRestDto gameRestDto) {
        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                List.of(new User(null, userRestDto1.firstname(), userRestDto1.lastname(),
                                userRestDto1.deactivated(), userRestDto1.token()),
                        new User(null, userRestDto2.firstname(), userRestDto2.lastname(),
                                userRestDto2.deactivated(), userRestDto2.token())));

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
