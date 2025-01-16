package com.gepardec.rest.impl;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static io.restassured.RestAssured.reset;
import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gepardec.RestTestFixtures;
import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import io.restassured.filter.log.LogDetail;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MatchResourceImplTest {

  ArrayList<String> usesMatchTokens = new ArrayList<>();
  ArrayList<String> usesUserTokens = new ArrayList<>();
  ArrayList<String> usesGameTokens = new ArrayList<>();

  final String USER_PATH = "/users";
  final String GAME_PATH = "/games";
  final String MATCH_PATH = "/matches";

  @BeforeAll
  public static void setup() {
    port = 8080;
    basePath = "gepardec-gamertrack/api/v1";
    enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
  }

  @AfterEach
  public void tearDown() {
    for (String token : usesMatchTokens) {
      delete("%s/%s".formatted(MATCH_PATH, token));
    }
    for (String token : usesGameTokens) {
      delete("%s/%s".formatted(GAME_PATH, token));
    }
    for (String token : usesUserTokens) {
      delete("%s/%s".formatted(USER_PATH, token));
    }
  }

  @AfterAll
  public static void cleanup() {
    reset();
  }

  @Test
  void ensureGetMatchesReturnsForNoMatches200OkWithEmptyList() {
    when().get(MATCH_PATH)
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body("", hasSize(0));
  }

  @Test
  void ensureGetMatchesReturnsForExistingMatches200OkWithMatchesList() {
    MatchRestDto createdMatch = createMatch();

    var foundMatches = when().get(MATCH_PATH)
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body("", hasSize(1))
        .extract()
        .jsonPath()
        .getList(".", MatchRestDto.class);

    foundMatches.getFirst().equals(createdMatch);
    usesMatchTokens.add(createdMatch.token());
  }

  @Test
  void ensureGetMatchesWithGameTokenAndWithoutUserTokenReturnsMatchReferencingTheSameGame()
      throws JsonProcessingException {
    GameRestDto createdGame = createGame();
    MatchRestDto matchThatShouldNotBeFound = createMatch(createUser(), createGame("someName"));
    MatchRestDto matchToBeFound1 = createMatch(createUser(), createdGame);
    MatchRestDto matchToBeFound2 = createMatch(createUser(), createdGame);

    var foundMatches =
        given()
            .queryParam("gameToken", createdGame.token())
            .when()
            .get(MATCH_PATH)
            .then()
            .statusCode(Status.OK.getStatusCode())
            .body("", hasSize(2))
            .extract()
            .jsonPath()
            .getList("", MatchRestDto.class);

    assertTrue(
        foundMatches.stream().map(MatchRestDto::token)
            .toList()
            .containsAll(List.of(matchToBeFound1.token(), matchToBeFound2.token())));
    assertFalse(foundMatches.stream()
        .anyMatch(match -> match.token().equals(matchThatShouldNotBeFound.token())));
  }

  @Test
  void ensureGetMatchesWithoutGameTokenAndWithUserTokenReturnsMatchReferencingTheSameUser() {
    UserRestDto createdUser = createUser();
    MatchRestDto matchThatShouldNotBeFound = createMatch(createUser(), createGame("someName"));
    MatchRestDto matchThatShouldBeFound1 = createMatch(createdUser, createGame("someName2"));
    MatchRestDto matchThatShouldBeFound2 = createMatch(createdUser, createGame());

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
        foundMatches.stream().map(MatchRestDto::token)
            .toList()
            .containsAll(
                List.of(matchThatShouldBeFound1.token(), matchThatShouldBeFound2.token())));
    assertFalse(foundMatches.stream()
        .anyMatch(match -> match.token().equals(matchThatShouldNotBeFound.token())));
  }

  @Test
  void ensureGetMatchesWithGameTokenAndUserTokenReturnsMatchReferencingTheSameGameAndUser() {
    UserRestDto createdUser = createUser();
    GameRestDto createdGame = createGame();
    MatchRestDto matchThatShouldNotBeFound = createMatch(createdUser, createGame("someName"));
    MatchRestDto matchThatNotBeFound2 = createMatch(createUser(), createdGame);
    MatchRestDto matchThatShouldBeFound1 = createMatch(createdUser, createdGame);
    MatchRestDto matchThatShouldBeFound2 = createMatch(createdUser, createdGame);

    var foundMatches = given().queryParam("gameToken", createdGame.token())
        .queryParam("userToken", createdUser.token())
        .when()
        .get(MATCH_PATH)
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body("", hasSize(2))
        .extract()
        .jsonPath()
        .getList("", MatchRestDto.class);

    assertEquals(2, foundMatches.stream().filter(
        match -> match.users().getFirst().getToken().equals(createdUser.token()) && match.game()
            .getToken().equals(createdGame.token())).count());
  }

  @Test
  void ensureGetMatchByTokenForExistingMatchReturnsMatch() {
    MatchRestDto existingMatch = createMatch();

    given().pathParam("token", existingMatch.token())
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
    UserRestDto userRestDto = createUser();

    CreateMatchCommand createMatchCommand = new CreateMatchCommand(
        new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
        List.of(new User(null, userRestDto.firstname(), userRestDto.lastname(),
            userRestDto.deactivated(), userRestDto.token())));

    MatchRestDto createdMatch = with()
        .body(createMatchCommand)
        .contentType("application/json")
        .post(MATCH_PATH)
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .body("token", notNullValue())
        .extract()
        .body()
        .as(MatchRestDto.class);

    assertEquals(createdMatch.game().getToken(), createMatchCommand.game().getToken());
    assertTrue(
        createdMatch.users().stream().map(User::getToken).toList()
            .containsAll(createMatchCommand.users().stream().map(User::getToken).toList()));

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
        .body(createMatchCommand)
        .contentType("application/json")
        .post(MATCH_PATH)
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  void ensureUpdateMatchForExistingMatchReturns200OkWithNewMatch() {
    MatchRestDto existingMatch = createMatch();
    UserRestDto userRestDto = createUser();
    UpdateMatchCommand matchToUpdate = new UpdateMatchCommand(
        new Game(existingMatch.game().getId(), existingMatch.game().getToken(),
            existingMatch.game().getName(),
            existingMatch.game().getRules()),
        List.of(
            existingMatch.users().getFirst(),
            new User(null, userRestDto.firstname(), userRestDto.lastname(),
                userRestDto.deactivated(), userRestDto.token())));

    var updatedMatch = given().pathParam("token", existingMatch.token())
        .contentType("application/json")
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
    UpdateGameCommand matchToUpdate = RestTestFixtures.updateGameCommand();
    given().pathParam("token", "12k31k2j3ksadj")
        .contentType("application/json")
        .body(matchToUpdate)
        .put("%s/{token}".formatted(MATCH_PATH))
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode());
  }

  @Test
  void ensureDeleteMatchForExistingMatchReturns200OkWithDeletedMatch() {
    MatchRestDto existingMatch = createMatch();

    MatchRestDto deletedMatch = given().pathParam("token", existingMatch.token())
        .delete("%s/{token}".formatted(MATCH_PATH))
        .then()
        .statusCode(Status.OK.getStatusCode())
        .extract()
        .as(MatchRestDto.class);

    assertEquals(existingMatch.token(), deletedMatch.token());
  }

  @Test
  void ensureDeleteMatchForNonExistingMatchReturns404NotFound() {
    given().pathParam("token", "12k31k2j3ksadj")
        .delete("%s/{token}".formatted(MATCH_PATH))
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode());
  }

  //-------------------HELPER METHODS -------------------------//

  public GameRestDto createGame(String name) {
    GameRestDto gameRestDto = with()
        .body(new CreateGameCommand(name, "no rules"))
        .contentType("application/json")
        .accept("application/json")
        .when().post(GAME_PATH)
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    usesGameTokens.add(gameRestDto.token());
    return gameRestDto;
  }

  public GameRestDto createGame() {
    return createGame("AnyName");
  }

  public UserRestDto createUser() {
    UserRestDto userRestDto = with()
        .contentType("application/json")
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

  public MatchRestDto createMatch() {
    return createMatch(createUser(), createGame());
  }

  public MatchRestDto createMatch(UserRestDto userRestDto, GameRestDto gameRestDto) {
    CreateMatchCommand createMatchCommand = new CreateMatchCommand(
        new Game(null, gameRestDto.token(), gameRestDto.name(), gameRestDto.rules()),
        List.of(new User(null, userRestDto.firstname(), userRestDto.lastname(),
            userRestDto.deactivated(), userRestDto.token())));

    MatchRestDto createdMatch = with()
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
