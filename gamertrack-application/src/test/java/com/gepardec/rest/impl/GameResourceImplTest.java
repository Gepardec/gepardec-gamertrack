package com.gepardec.rest.impl;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.port;
import static io.restassured.RestAssured.reset;
import static io.restassured.RestAssured.when;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gepardec.RestTestFixtures;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.restassured.filter.log.LogDetail;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GameResourceImplTest {

  ArrayList<String> usesTokens = new ArrayList<>();

  @BeforeAll
  public static void setup() {
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
  void ensureGetGamesWithNoExistingGamesReturns200OkWithEmptyList() {
    when().get()
        .then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  @Test
  void ensureGetGamesWithExistingGameReturns200OkWithListContainingGame() {
    //GIVEN
    GameRestDto gameRestDto = with()
        .body(new CreateGameCommand("news game", "no rules"))
        .contentType("application/json")
        .accept("application/json")
        .when().post()
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    //WHEN THEN
    when()
        .get()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .jsonPath()
        .getList("", GameRestDto.class)
        .equals(gameRestDto);

    usesTokens.add(gameRestDto.token());
  }

  @Test
  void ensureGetGameWithExistingGameReturnsGame() {
    GameRestDto gameRestDto = with()
        .body(new CreateGameCommand("TestGetGame", "no rules"))
        .contentType("application/json")
        .accept("application/json")
        .when().post()
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    var foundGame = when()
        .get("/%s".formatted(gameRestDto.token()))
        .then()
        .statusCode(Status.OK.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    assertEquals(foundGame, gameRestDto);
    usesTokens.add(gameRestDto.token());
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

    var responeObjectFromRequest = with()
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
    GameRestDto gameRestDto = with()
        .body(new CreateGameCommand("TestGetGame", "no rules"))
        .contentType("application/json")
        .accept("application/json")
        .when().post()
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    UpdateGameCommand gameToBeUpdated = new UpdateGameCommand("UpatedTestGame", "still no rules");

    with()
        .body(gameToBeUpdated)
        .contentType("application/json")
        .put("/%s".formatted(gameRestDto.token()))
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body("token", samePropertyValuesAs(gameRestDto.token()))
        .body("name", samePropertyValuesAs(gameToBeUpdated.name()))
        .body("rules", samePropertyValuesAs(gameToBeUpdated.rules()));

    usesTokens.add(gameRestDto.token());
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

  @Test
  void ensureDeleteExistingGameReturns200OkWithDeletedGame() {
    GameRestDto gameRestDto = with()
        .body(new CreateGameCommand("TestGetGame", "no rules"))
        .contentType("application/json")
        .accept("application/json")
        .when().post()
        .then()
        .statusCode(Status.CREATED.getStatusCode())
        .extract()
        .body()
        .as(GameRestDto.class);

    with()
        .delete("/%s".formatted(gameRestDto.token()))
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body("token", samePropertyValuesAs(gameRestDto.token()))
        .body("name", samePropertyValuesAs(gameRestDto.name()))
        .body("rules", samePropertyValuesAs(gameRestDto.rules()));
  }

}