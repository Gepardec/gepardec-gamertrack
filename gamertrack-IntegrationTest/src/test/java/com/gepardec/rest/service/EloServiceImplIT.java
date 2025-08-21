package com.gepardec.rest.service;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.dto.ScoreRestDto;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class EloServiceImplIT {

    static List<String> usesGameTokens = new ArrayList<>();
    static List<String> usesUserTokens = new ArrayList<>();

    static String authHeader;
    String bearerToken;

    @ConfigProperty(name = "SECRET_DEFAULT_PW")
    String SECRET_DEFAULT_PW;
    @ConfigProperty(name = "SECRET_ADMIN_NAME")
    String SECRET_ADMIN_NAME;

    @BeforeAll
    public static void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
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
                    .request("DELETE", "api/v1/games/{token}")
            ;
        }
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
                    .request("DELETE", "api/v1/users/{token}")
            ;
        }
    }

    @Test
    public void ensureStartMatchChangesEloScoreCorrectly() {
        CreateGameCommand gameCommand = new CreateGameCommand("4Gewinnt", "Nicht schummeln");

        CreateUserCommand userCommand1 = new CreateUserCommand("Max","Muster");
        CreateUserCommand userCommand2 = new CreateUserCommand("Jakob","Mayer");

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

        String gameToken1 = with()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(gameCommand)
                .contentType("application/json")
                .when()
                .request("POST", "api/v1/games")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("token", notNullValue()).extract()
                .path("token");
        usesGameTokens.add(gameToken1);

        String userToken1 = with().when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(userCommand1)
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .assertThat()
                .body("firstname", equalTo("Max"))
                .extract()
                .path("token");
        usesUserTokens.add(userToken1);

        String userToken2 = with().when()
                .contentType("application/json")
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(userCommand2)
                .request("POST", "api/v1/users")
                .then()
                .statusCode(201)
                .assertThat()
                .body("firstname", equalTo("Jakob"))
                .extract()
                .path("token");
        usesUserTokens.add(userToken2);

        CreateMatchCommand createMatchCommandUser1Wins = new CreateMatchCommand(
                new Game(null, gameToken1, "4Gewinnt", "Nicht schummeln"),
                List.of(new User(0L, "Max","Muster",
                       false, userToken1),new User(0L, "Jakob","Mayer",
                        false, userToken2)));

        CreateMatchCommand createMatchCommandUser2Wins = new CreateMatchCommand(
                new Game(null, gameToken1, "4Gewinnt", "Nicht schummeln"),
                List.of(new User(0L, "Jakob","Mayer",false, userToken2)
                        , new User(0L, "Max","Muster",false, userToken1)));

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                        .body(createMatchCommandUser1Wins)
                        .contentType("application/json")
                        .request("POST", "api/v1/matches")
                        .then()
                        .statusCode(Response.Status.CREATED.getStatusCode())
                        .body("token", notNullValue());



        var foundScores = with()
                        .contentType("application/json")
                        .request("GET", "api/v1/scores/?game=" + gameToken1)
                        .then()
                        .statusCode(Response.Status.OK.getStatusCode())
                        .extract()
                        .jsonPath()
                        .getList("", ScoreRestDto.class);

        assertTrue(foundScores.stream().anyMatch(
                score -> score.user().token().equals(userToken1) &&
                        score.score() == 1516.0));

        assertTrue(foundScores.stream().anyMatch(
                score -> score.user().token().equals(userToken2) &&
                        score.score() == 1484.0));

        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                        .body(createMatchCommandUser1Wins)
                        .contentType("application/json")
                        .request("POST", "api/v1/matches")
                        .then()
                        .body("token", notNullValue());

        var foundScores2 = with()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?game=" + gameToken1)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("", ScoreRestDto.class);

        assertTrue(foundScores2.stream().anyMatch(
                score -> score.user().token().equals(userToken1) &&
                        score.score() == 1531.0));

        assertTrue(foundScores2.stream().anyMatch(
                score -> score.user().token().equals(userToken2) &&
                        score.score() == 1469.0));


        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                        .body(createMatchCommandUser1Wins)
                        .contentType("application/json")
                        .request("POST", "api/v1/matches")
                        .then()
                        .body("token", notNullValue());

        var foundScores3 = with()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?game=" + gameToken1)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("", ScoreRestDto.class);

        assertTrue(foundScores3.stream().anyMatch(
                score -> score.user().token().equals(userToken1) &&
                        score.score() == 1544.0));

        assertTrue(foundScores3.stream().anyMatch(
                score -> score.user().token().equals(userToken2) &&
                        score.score() == 1456.0));


        with()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON)
                .body(createMatchCommandUser2Wins)
                .contentType("application/json")
                .request("POST", "api/v1/matches")
                .then()
                .body("token", notNullValue());

        var foundScores4 = with()
                .contentType("application/json")
                .request("GET", "api/v1/scores/?game=" + gameToken1)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("", ScoreRestDto.class);

        assertTrue(foundScores4.stream().anyMatch(
                score -> score.user().token().equals(userToken1) &&
                        score.score() == 1524.0));

        assertTrue(foundScores4.stream().anyMatch(
                score -> score.user().token().equals(userToken2) &&
                        score.score() == 1476.0));
    }
}
