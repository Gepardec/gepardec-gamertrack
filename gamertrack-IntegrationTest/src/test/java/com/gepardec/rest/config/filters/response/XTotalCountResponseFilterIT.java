package com.gepardec.rest.config.filters.response;

import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.restassured.filter.log.LogDetail;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.nullValue;

public class XTotalCountResponseFilterIT {


    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1/games";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);


    }

    @AfterAll
    public static void cleanup() {
        reset();
    }

    @Test
    void ensureXTotalCountHeaderEqualsReturnedObjectAmount() {
        var extractedResponse = when()
                .get()
                .then()
                .statusCode(200)
                .extract();

        var xTotalCount = extractedResponse
                .header("X-Total-Count");

        var games = extractedResponse
                .body()
                .jsonPath()
                .getList("", GameRestDto.class);

        Assertions.assertEquals(games.size(), Integer.parseInt(xTotalCount));
    }

    @Test
    void ensureXTotalCountHeaderIsNotPresentIfBodyIsEmpty() {
        when()
                .get("/aksdjfalsskjd")
                .then()
                .statusCode(404)
                .header("X-Total-Count", nullValue());
    }

    @Test
    void ensureXTotalCountHeaderIsNotPresentForEndpointsThatReturnsNoList() {
        //GIVEN
        GameRestDto existingGame =
                with()
                        .body(new CreateGameCommand("news game2000100", "no rules"))
                        .contentType("application/json")
                        .accept("application/json")
                        .when()
                        .post()
                        .then()
                        .extract()
                        .body()
                        .as(GameRestDto.class);

        //WHEN THEN
        when()
                .get("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(200)
                .header("X-Total-Count", nullValue());

        //TEARDOWN
        with()
                .delete("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
