package com.gepardec.rest.impl;

import com.gepardec.model.TournamentState;
import com.gepardec.rest.model.command.AddTournamentParticipantCommand;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateTournamentCommand;
import com.gepardec.rest.model.command.CreateTournamentMatchCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateTournamentStateCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.ScoreRestDto;
import com.gepardec.rest.model.dto.TournamentRestDto;
import com.gepardec.rest.model.dto.TournamentStandingRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
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
import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class TournamentResourceImplIT {

    ArrayList<String> usesTournamentTokens = new ArrayList<>();
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
    final String SCORE_PATH = "/scores";
    final String TOURNAMENT_PATH = "/tournaments";

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
        for (String token : usesTournamentTokens) {
            authed()
                    .pathParam("token", token)
                    .request("DELETE", "%s/{token}".formatted(TOURNAMENT_PATH));
        }
        usesTournamentTokens.clear();
        for (String token : usesGameTokens) {
            authed()
                    .pathParam("token", token)
                    .request("DELETE", "%s/{token}".formatted(GAME_PATH));
        }
        usesGameTokens.clear();
        for (String token : usesUserTokens) {
            authed()
                    .pathParam("token", token)
                    .request("DELETE", "%s/{token}".formatted(USER_PATH));
        }
        usesUserTokens.clear();
    }

    @AfterAll
    public static void cleanup() {
        reset();
    }

    //Functional test 1: valid creation, shows up in list and detail view
    @Test
    void ensureCreateTournamentWithValidInputReturnsCreatedTournamentAndShowsUpInListAndDetail() {
        GameRestDto game = createGame();
        List<UserRestDto> users = List.of(createUser(), createUser(), createUser(), createUser());

        TournamentRestDto createdTournament = createTournament("Office Darts Cup", game, users);

        assertEquals("Office Darts Cup", createdTournament.name());
        assertEquals(TournamentState.CREATED.name(), createdTournament.state());
        assertEquals(game.token(), createdTournament.game().token());
        assertEquals(users.stream().map(UserRestDto::token).sorted().toList(),
                createdTournament.participants().stream().map(UserRestDto::token).sorted().toList());

        var foundTournaments = when()
                .get(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList(".", TournamentRestDto.class);

        assertTrue(foundTournaments.stream()
                .anyMatch(tournament -> tournament.token().equals(createdTournament.token())));

        given()
                .pathParam("token", createdTournament.token())
                .when()
                .get("%s/{token}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", equalTo(createdTournament.token()))
                .body("state", equalTo(TournamentState.CREATED.name()));
    }

    //Functional test 2: garbage input is rejected
    @Test
    void ensureCreateTournamentWithoutNameReturns400BadRequest() {
        GameRestDto game = createGame();
        List<String> participants = List.of(createUser().token(), createUser().token());

        authed()
                .body(new CreateTournamentCommand("  ", game.token(), participants))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureCreateTournamentWithUnknownGameReturns400BadRequest() {
        List<String> participants = List.of(createUser().token(), createUser().token());

        authed()
                .body(new CreateTournamentCommand("Office Darts Cup", "unknownGameToken", participants))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureCreateTournamentWithUnknownUserReturns400BadRequest() {
        GameRestDto game = createGame();
        List<String> participants = List.of(createUser().token(), "unknownUserToken");

        authed()
                .body(new CreateTournamentCommand("Office Darts Cup", game.token(), participants))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureCreateTournamentWithOnlyOneParticipantReturns400BadRequest() {
        GameRestDto game = createGame();

        authed()
                .body(new CreateTournamentCommand("Office Darts Cup", game.token(),
                        List.of(createUser().token())))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureCreateTournamentWithSameUserTwiceReturns400BadRequest() {
        GameRestDto game = createGame();
        UserRestDto user = createUser();

        authed()
                .body(new CreateTournamentCommand("Office Darts Cup", game.token(),
                        List.of(user.token(), user.token())))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    //Functional test 3: participant list is only editable while CREATED
    @Test
    void ensureAddAndRemoveParticipantWorksWhileCreatedAndIsRejectedOnceRunning() {
        TournamentRestDto tournament = createTournament();
        UserRestDto additionalUser = createUser();

        var tournamentWithAddedParticipant = authed()
                .pathParam("token", tournament.token())
                .body(new AddTournamentParticipantCommand(additionalUser.token()))
                .post("%s/{token}/participants".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(TournamentRestDto.class);

        assertTrue(tournamentWithAddedParticipant.participants().stream()
                .anyMatch(participant -> participant.token().equals(additionalUser.token())));

        var tournamentWithRemovedParticipant = authed()
                .pathParam("token", tournament.token())
                .pathParam("userToken", additionalUser.token())
                .delete("%s/{token}/participants/{userToken}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .as(TournamentRestDto.class);

        assertFalse(tournamentWithRemovedParticipant.participants().stream()
                .anyMatch(participant -> participant.token().equals(additionalUser.token())));

        moveTournamentToState(tournament.token(), TournamentState.RUNNING);

        authed()
                .pathParam("token", tournament.token())
                .body(new AddTournamentParticipantCommand(additionalUser.token()))
                .post("%s/{token}/participants".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        authed()
                .pathParam("token", tournament.token())
                .pathParam("userToken", tournament.participants().getFirst().token())
                .delete("%s/{token}/participants/{userToken}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    //Functional test 4: tournament match is linked and changes ratings exactly like a normal match
    @Test
    void ensureTournamentMatchIsLinkedToTournamentAndChangesRatingsLikeNormalMatch() {
        GameRestDto game = createGame();
        UserRestDto tournamentWinner = createUser();
        UserRestDto tournamentLoser = createUser();
        TournamentRestDto tournament =
                createTournament("Office Darts Cup", game, List.of(tournamentWinner, tournamentLoser));
        moveTournamentToState(tournament.token(), TournamentState.RUNNING);

        double winnerScoreBefore = scoreOf(tournamentWinner, game);
        double loserScoreBefore = scoreOf(tournamentLoser, game);

        MatchRestDto tournamentMatch = createTournamentMatch(tournament.token(),
                List.of(tournamentWinner.token(), tournamentLoser.token()));

        //The match is linked to the tournament
        var tournamentMatches = given()
                .pathParam("token", tournament.token())
                .when()
                .get("%s/{token}/matches".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList(".", MatchRestDto.class);

        assertEquals(1, tournamentMatches.size());
        assertEquals(tournamentMatch.token(), tournamentMatches.getFirst().token());

        //It is a normal match at the same time
        given()
                .pathParam("token", tournamentMatch.token())
                .when()
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", equalTo(tournamentMatch.token()));

        double tournamentWinnerDelta = scoreOf(tournamentWinner, game) - winnerScoreBefore;
        double tournamentLoserDelta = scoreOf(tournamentLoser, game) - loserScoreBefore;

        //A normal match between two fresh users with the same default rating must change
        //the ratings in exactly the same way
        UserRestDto normalWinner = createUser();
        UserRestDto normalLoser = createUser();
        double normalWinnerScoreBefore = scoreOf(normalWinner, game);
        double normalLoserScoreBefore = scoreOf(normalLoser, game);

        createNormalMatch(game, normalWinner, normalLoser);

        assertEquals(scoreOf(normalWinner, game) - normalWinnerScoreBefore, tournamentWinnerDelta);
        assertEquals(scoreOf(normalLoser, game) - normalLoserScoreBefore, tournamentLoserDelta);
        assertTrue(tournamentWinnerDelta > 0);
        assertTrue(tournamentLoserDelta < 0);
    }

    //Functional test 5: only registered participants may play
    @Test
    void ensureTournamentMatchWithNonParticipantReturns400BadRequest() {
        TournamentRestDto tournament = createTournament();
        moveTournamentToState(tournament.token(), TournamentState.RUNNING);
        UserRestDto nonParticipant = createUser();

        authed()
                .pathParam("token", tournament.token())
                .body(new CreateTournamentMatchCommand(
                        List.of(tournament.participants().getFirst().token(), nonParticipant.token())))
                .post("%s/{token}/matches".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void ensureTournamentMatchInNotRunningTournamentReturns400BadRequest() {
        TournamentRestDto tournament = createTournament();

        authed()
                .pathParam("token", tournament.token())
                .body(new CreateTournamentMatchCommand(
                        List.of(tournament.participants().getFirst().token(),
                                tournament.participants().getLast().token())))
                .post("%s/{token}/matches".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    }

    //Functional test 6: standings list all participants in the documented order
    @Test
    void ensureStandingsListAllParticipantsOrderedByWinsAndMatchesPlayed() {
        GameRestDto game = createGame();
        UserRestDto userA = createUser();
        UserRestDto userB = createUser();
        UserRestDto userC = createUser();
        UserRestDto userD = createUser();
        TournamentRestDto tournament =
                createTournament("Office Darts Cup", game, List.of(userA, userB, userC, userD));
        moveTournamentToState(tournament.token(), TournamentState.RUNNING);

        createTournamentMatch(tournament.token(), List.of(userA.token(), userB.token()));
        createTournamentMatch(tournament.token(), List.of(userA.token(), userC.token()));
        createTournamentMatch(tournament.token(), List.of(userB.token(), userA.token()));

        var standings = given()
                .pathParam("token", tournament.token())
                .when()
                .get("%s/{token}/standings".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList(".", TournamentStandingRestDto.class);

        assertEquals(4, standings.size());

        assertEquals(userA.token(), standings.get(0).user().token());
        assertEquals(2, standings.get(0).wins());
        assertEquals(3, standings.get(0).matchesPlayed());

        assertEquals(userB.token(), standings.get(1).user().token());
        assertEquals(1, standings.get(1).wins());
        assertEquals(2, standings.get(1).matchesPlayed());

        assertEquals(userC.token(), standings.get(2).user().token());
        assertEquals(0, standings.get(2).wins());
        assertEquals(1, standings.get(2).matchesPlayed());

        assertEquals(userD.token(), standings.get(3).user().token());
        assertEquals(0, standings.get(3).wins());
        assertEquals(0, standings.get(3).matchesPlayed());
    }

    //Functional test 7: the state never moves backwards
    @Test
    void ensureMovingTournamentStateBackwardsReturns400BadRequest() {
        TournamentRestDto tournament = createTournament();

        moveTournamentToState(tournament.token(), TournamentState.RUNNING);

        authed()
                .pathParam("token", tournament.token())
                .body(new UpdateTournamentStateCommand(TournamentState.CREATED))
                .put("%s/{token}/state".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        moveTournamentToState(tournament.token(), TournamentState.DONE);

        authed()
                .pathParam("token", tournament.token())
                .body(new UpdateTournamentStateCommand(TournamentState.RUNNING))
                .put("%s/{token}/state".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        given()
                .pathParam("token", tournament.token())
                .when()
                .get("%s/{token}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("state", equalTo(TournamentState.DONE.name()));
    }

    //Functional test 8: deleting a tournament keeps users, game, matches and ratings
    @Test
    void ensureDeleteRunningTournamentKeepsUsersGameMatchesAndRatings() {
        GameRestDto game = createGame();
        UserRestDto winner = createUser();
        UserRestDto loser = createUser();
        TournamentRestDto tournament =
                createTournament("Office Darts Cup", game, List.of(winner, loser));
        moveTournamentToState(tournament.token(), TournamentState.RUNNING);

        MatchRestDto tournamentMatch =
                createTournamentMatch(tournament.token(), List.of(winner.token(), loser.token()));
        double winnerScoreAfterMatch = scoreOf(winner, game);

        authed()
                .pathParam("token", tournament.token())
                .delete("%s/{token}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("token", equalTo(tournament.token()));

        given()
                .pathParam("token", tournament.token())
                .when()
                .get("%s/{token}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());

        //Match, users, game and rating changes are untouched
        given()
                .pathParam("token", tournamentMatch.token())
                .when()
                .get("%s/{token}".formatted(MATCH_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode());

        authed()
                .pathParam("token", winner.token())
                .when()
                .get("%s/{token}".formatted(USER_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode());

        given()
                .pathParam("token", game.token())
                .when()
                .get("%s/{token}".formatted(GAME_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode());

        assertEquals(winnerScoreAfterMatch, scoreOf(winner, game));
    }

    @Test
    void ensureGetTournamentByTokenForNonExistingTournamentReturns404NotFound() {
        given()
                .pathParam("token", "nonExistingToken")
                .when()
                .get("%s/{token}".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void ensureCreateTournamentWithoutAuthenticationReturns401Unauthorized() {
        GameRestDto game = createGame();
        List<String> participants = List.of(createUser().token(), createUser().token());

        with()
                .contentType("application/json")
                .body(new CreateTournamentCommand("Office Darts Cup", game.token(), participants))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.UNAUTHORIZED.getStatusCode());
    }

    //-------------------HELPER METHODS -------------------------//

    private RequestSpecification authed() {
        return with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .contentType("application/json");
    }

    public UserRestDto createUser() {
        UserRestDto userRestDto = authed()
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
        GameRestDto gameRestDto = authed()
                .body(new CreateGameCommand("default Game", "no rules"))
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

    public TournamentRestDto createTournament() {
        return createTournament("Office Darts Cup", createGame(),
                List.of(createUser(), createUser(), createUser()));
    }

    public TournamentRestDto createTournament(String name, GameRestDto game,
                                              List<UserRestDto> participants) {
        TournamentRestDto tournamentRestDto = authed()
                .body(new CreateTournamentCommand(name, game.token(),
                        participants.stream().map(UserRestDto::token).toList()))
                .post(TOURNAMENT_PATH)
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .body()
                .as(TournamentRestDto.class);

        usesTournamentTokens.add(tournamentRestDto.token());
        return tournamentRestDto;
    }

    public void moveTournamentToState(String tournamentToken, TournamentState state) {
        authed()
                .pathParam("token", tournamentToken)
                .body(new UpdateTournamentStateCommand(state))
                .put("%s/{token}/state".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.OK.getStatusCode())
                .body("state", equalTo(state.name()));
    }

    public MatchRestDto createTournamentMatch(String tournamentToken, List<String> userTokens) {
        return authed()
                .pathParam("token", tournamentToken)
                .body(new CreateTournamentMatchCommand(userTokens))
                .post("%s/{token}/matches".formatted(TOURNAMENT_PATH))
                .then()
                .statusCode(Status.CREATED.getStatusCode())
                .extract()
                .body()
                .as(MatchRestDto.class);
    }

    public void createNormalMatch(GameRestDto game, UserRestDto winner, UserRestDto loser) {
        authed()
                .body(new com.gepardec.rest.model.command.CreateMatchCommand(
                        new com.gepardec.model.Game(null, game.token(), game.name(), game.rules()),
                        List.of(new com.gepardec.model.User(null, winner.firstname(), winner.lastname(),
                                        winner.deactivated(), winner.token()),
                                new com.gepardec.model.User(null, loser.firstname(), loser.lastname(),
                                        loser.deactivated(), loser.token()))))
                .post(MATCH_PATH)
                .then()
                .statusCode(Status.CREATED.getStatusCode());
    }

    public double scoreOf(UserRestDto user, GameRestDto game) {
        var scores = given()
                .queryParam("user", user.token())
                .queryParam("game", game.token())
                .queryParam("includeDeactivated", true)
                .when()
                .get(SCORE_PATH)
                .then()
                .statusCode(Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList(".", ScoreRestDto.class);

        assertNotNull(scores);
        assertFalse(scores.isEmpty(),
                "Expected a score for user %s and game %s".formatted(user.token(), game.token()));
        return scores.getFirst().score();
    }
}
