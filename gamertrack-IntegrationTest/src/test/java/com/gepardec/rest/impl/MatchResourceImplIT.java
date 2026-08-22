package com.gepardec.rest.impl;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.*;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.Response.Status;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static java.lang.Math.ceil;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class MatchResourceImplIT {

    ArrayList<String> usesMatchTokens = new ArrayList<>();
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
                    .body(new AuthCredentialCommand(SECRET_ADMIN_NAME,SECRET_DEFAULT_PW))
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
    void ensureGetMatchesWithPaginationReturnsPaginatedMatches() {
        UserRestDto createdUser = createUser();
        GameRestDto createdGame = createGame();
        int existingMatchCount = Integer.parseInt(given()
                .queryParam("gameToken", createdGame.token())
                .when()
                .head(MATCH_PATH)
                .header("X-Total-Count"));

        var newAddedMatches = List.of(
                createMatch(createUser(), createUser(), createdGame),
                createMatch(createdUser, createUser(), createdGame),
                createMatch(createdUser, createUser(), createdGame));

        var foundMatches = given()
                .queryParam("gameToken", createdGame.token())
                .queryParam("pageSize", 3)
                .queryParam("pageNumber", 1)
                .when()
                .get(MATCH_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .header("X-Total-Count", String.valueOf(newAddedMatches.size() + existingMatchCount))
                .header("X-Total-Pages", String.valueOf((int) ceil((existingMatchCount + newAddedMatches.size()) / 4.0)))
                .header("X-Page-Size", "3")
                .header("X-Current-Page", "1")
                .extract()
                .jsonPath()
                .getList("", MatchRestDto.class);

        assertEquals(3, foundMatches.size());
        assertTrue(foundMatches.containsAll(newAddedMatches));
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
                                userRestDto2.deactivated(), userRestDto2.token())),
                Map.of(userRestDto1.token(), 1, userRestDto2.token(), 2));

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
        assertEquals(createMatchCommand.outcome(), createdMatch.outcome());
        usesMatchTokens.add(createdMatch.token());
    }

    @Test
    void ensureCreateMatchForInvalidMatchReturns400BadRequest() {
        UserRestDto userRestDto = createUser();

        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, null, "anything", "should fail"),
                List.of(new User(null, userRestDto.firstname(), userRestDto.lastname(),
                        userRestDto.deactivated(), userRestDto.token())),
                Map.of(userRestDto.token(), 1));

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
                                userRestDto.deactivated(), userRestDto.token())),
                Map.of(existingMatch.users().getFirst().token(), 1, userRestDto.token(), 2));

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

    @Test
    void ensureCreateMatchWithFourPlayersStoresAllPlacements() {
        GameRestDto createdGame = createGame();
        List<UserRestDto> createdUsers = List.of(createUser(), createUser(), createUser(), createUser());

        Map<String, Integer> outcome = new HashMap<>();
        for (int i = 0; i < createdUsers.size(); i++) {
            outcome.put(createdUsers.get(i).token(), i + 1);
        }

        MatchRestDto createdMatch = postMatch(createMatchCommand(createdGame, createdUsers, outcome))
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .as(MatchRestDto.class);
        usesMatchTokens.add(createdMatch.token());

        assertEquals(outcome, createdMatch.outcome());
    }

    @Test
    void ensureCreateMatchWithDrawReturnsDrawOutcomeAndKeepsEqualRatingsUnchanged() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();

        MatchRestDto createdMatch = createMatch(createdUser1, createdUser2, createdGame,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 1));

        assertEquals(Map.of(createdUser1.token(), 1, createdUser2.token(), 1), createdMatch.outcome());

        //both users started with the same default rating, so a draw must not change it
        with()
                .contentType("application/json")
                .request("GET", "/scores/?game=" + createdGame.token())
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("score", everyItem(equalTo(1500.0f)));
    }

    @Test
    void ensureCreateMatchWithPlacementsDifferingFromUserOrderFollowsThePlacements() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();

        //the first user in the list is NOT the winner - the placements decide
        MatchRestDto createdMatch = createMatch(createdUser1, createdUser2, createdGame,
                Map.of(createdUser1.token(), 2, createdUser2.token(), 1));

        assertEquals(2, createdMatch.outcome().get(createdUser1.token()));
        assertEquals(1, createdMatch.outcome().get(createdUser2.token()));

        var foundScores = with()
                .contentType("application/json")
                .request("GET", "/scores/?game=" + createdGame.token())
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath();

        assertEquals(1516.0f, foundScores.getFloat(
                "find { it.user.token == '%s' }.score".formatted(createdUser2.token())));
        assertEquals(1484.0f, foundScores.getFloat(
                "find { it.user.token == '%s' }.score".formatted(createdUser1.token())));
    }

    @Test
    void ensureGetMatchByTokenReturnsExactlyTheEnteredPlacements() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();

        MatchRestDto createdMatch = createMatch(createdUser1, createdUser2, createdGame,
                Map.of(createdUser1.token(), 2, createdUser2.token(), 1));

        MatchRestDto foundMatch = given()
                .pathParam("token", createdMatch.token())
                .when()
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(MatchRestDto.class);

        assertEquals(Map.of(createdUser1.token(), 2, createdUser2.token(), 1), foundMatch.outcome());
    }

    @Test
    void ensureCreateMatchWithInvalidOutcomeReturns400BadRequest() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();
        List<UserRestDto> createdUsers = List.of(createdUser1, createdUser2);

        //no outcome at all
        postMatch(createMatchCommand(createdGame, createdUsers, null))
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        //participant without placement
        postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 1)))
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        //placement for a user that is not part of the match
        postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 1, "notAParticipant", 2)))
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        //gap that is not caused by a tie
        postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 3)))
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        //placement below 1
        postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 0, createdUser2.token(), 1)))
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureCreateMatchWithTieNotSkippingFollowingPlacementReturns400BadRequest() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();
        UserRestDto createdUser3 = createUser();
        List<UserRestDto> createdUsers = List.of(createdUser1, createdUser2, createdUser3);

        //two players share first place, so the next placement has to be 3 - not 2
        postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 1, createdUser3.token(), 2)))
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        MatchRestDto createdMatch = postMatch(createMatchCommand(createdGame, createdUsers,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 1, createdUser3.token(), 3)))
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .as(MatchRestDto.class);
        usesMatchTokens.add(createdMatch.token());

        assertEquals(Map.of(createdUser1.token(), 1, createdUser2.token(), 1, createdUser3.token(), 3),
                createdMatch.outcome());
    }

    @Test
    void ensureUpdateMatchCorrectingTheOutcomeStoresTheNewOutcome() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();

        MatchRestDto existingMatch = createMatch(createdUser1, createdUser2, createdGame,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 2));

        //the wrong winner was entered - swap the placements
        UpdateMatchCommand matchToUpdate = new UpdateMatchCommand(
                new Game(null, createdGame.token(), createdGame.name(), createdGame.rules()),
                List.of(new User(null, createdUser1.firstname(), createdUser1.lastname(),
                                createdUser1.deactivated(), createdUser1.token()),
                        new User(null, createdUser2.firstname(), createdUser2.lastname(),
                                createdUser2.deactivated(), createdUser2.token())),
                Map.of(createdUser1.token(), 2, createdUser2.token(), 1));

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
                .statusCode(Status.OK.getStatusCode());

        MatchRestDto foundMatch = given()
                .pathParam("token", existingMatch.token())
                .when()
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(MatchRestDto.class);

        assertEquals(matchToUpdate.outcome(), foundMatch.outcome());
    }

    @Test
    void ensureUpdateMatchWithInvalidOutcomeReturns400BadRequest() {
        GameRestDto createdGame = createGame();
        UserRestDto createdUser1 = createUser();
        UserRestDto createdUser2 = createUser();

        MatchRestDto existingMatch = createMatch(createdUser1, createdUser2, createdGame,
                Map.of(createdUser1.token(), 1, createdUser2.token(), 2));

        UpdateMatchCommand matchToUpdate = new UpdateMatchCommand(
                new Game(null, createdGame.token(), createdGame.name(), createdGame.rules()),
                List.of(new User(null, createdUser1.firstname(), createdUser1.lastname(),
                                createdUser1.deactivated(), createdUser1.token()),
                        new User(null, createdUser2.firstname(), createdUser2.lastname(),
                                createdUser2.deactivated(), createdUser2.token())),
                Map.of(createdUser1.token(), 1, createdUser2.token(), 3));

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
                .statusCode(Status.BAD_REQUEST.getStatusCode());
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

    public CreateMatchCommand createMatchCommand(GameRestDto gameRestDto, List<UserRestDto> userRestDtos,
                                                 Map<String, Integer> outcome) {
        return new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                userRestDtos.stream()
                        .map(u -> new User(null, u.firstname(), u.lastname(), u.deactivated(), u.token()))
                        .toList(),
                outcome);
    }

    public ValidatableResponse postMatch(CreateMatchCommand createMatchCommand) {
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
                .then();
    }

    public MatchRestDto createMatch(UserRestDto userRestDto1, UserRestDto userRestDto2, GameRestDto gameRestDto) {
        return createMatch(userRestDto1, userRestDto2, gameRestDto,
                Map.of(userRestDto1.token(), 1, userRestDto2.token(), 2));
    }

    public MatchRestDto createMatch(UserRestDto userRestDto1, UserRestDto userRestDto2, GameRestDto gameRestDto,
                                    Map<String, Integer> outcome) {
        CreateMatchCommand createMatchCommand = new CreateMatchCommand(
                new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
                List.of(new User(null, userRestDto1.firstname(), userRestDto1.lastname(),
                                userRestDto1.deactivated(), userRestDto1.token()),
                        new User(null, userRestDto2.firstname(), userRestDto2.lastname(),
                                userRestDto2.deactivated(), userRestDto2.token())),
                outcome);

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
