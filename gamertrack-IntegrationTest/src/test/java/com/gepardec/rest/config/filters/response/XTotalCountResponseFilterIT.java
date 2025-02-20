package com.gepardec.rest.config.filters.response;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.nullValue;

public class XTotalCountResponseFilterIT {

    static Dotenv dotenv = Dotenv.configure().directory("../").filename("secret.env").ignoreIfMissing().load();
    private static final String SECRET_DEFAULT_PW = dotenv.get("SECRET_DEFAULT_PW", System.getenv("SECRET_DEFAULT_PW"));
    private static final String SECRET_ADMIN_NAME = dotenv.get("SECRET_ADMIN_NAME", System.getenv("SECRET_ADMIN_NAME"));

    private static RequestSpecification requestSpecification;


    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);

        //Login & get JWT Token
        String jwtToken = given()
                .body(new AuthCredentialCommand(SECRET_ADMIN_NAME, SECRET_DEFAULT_PW))
                .contentType("application/json")
                .accept("application/json")
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .path("token");

        RequestSpecBuilder builder = new RequestSpecBuilder();

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);
        headers.put("Content-Type", String.valueOf(ContentType.JSON));
        headers.put("Accept", String.valueOf(ContentType.JSON));

        builder.addHeaders(headers);
        builder.setBasePath("gepardec-gamertrack/api/v1/games");
        requestSpecification = builder.build();
    }

    @AfterAll
    public static void cleanup() {
        reset();
    }

    @Test
    void ensureXTotalCountHeaderEqualsReturnedObjectAmount() {
        var extractedResponse = given(requestSpecification)
                .when()
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
        given(requestSpecification)
                .when()
                .get("/aksdjfalsskjd")
                .then()
                .statusCode(404)
                .header("X-Total-Count", nullValue());
    }

    @Test
    void ensureXTotalCountHeaderIsNotPresentForEndpointsThatReturnsNoList() {
        //GIVEN
        GameRestDto existingGame =
                given(requestSpecification)
                        .with()
                        .body(new CreateGameCommand("nelkkjkkjjljws game2000100", "no rules"))
                        .contentType("application/json")
                        .accept("application/json")
                        .when()
                        .post()
                        .then()
                        .extract()
                        .as(GameRestDto.class);

        //WHEN THEN
        given(requestSpecification)
                .when()
                .get("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(200)
                .header("X-Total-Count", nullValue());

        //TEARDOWN
        given(requestSpecification)
                .with()
                .delete("/%s".formatted(existingGame.token()))
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
