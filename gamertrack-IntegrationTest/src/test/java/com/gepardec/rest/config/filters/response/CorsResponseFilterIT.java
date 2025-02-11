package com.gepardec.rest.config.filters.response;

import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.nullValue;

public class CorsResponseFilterIT {

    private final String VALID_ORIGIN = "http://gamertrack-frontend.apps.cloudscale-lpg-2.appuio.cloud";
    private final String INVALID_ORIGIN = "http://lkadsjlksjdfgamertrack-frontend.apps.cloudscale-lpg-2.appuio.com";
    private final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, HEAD";
    private final String ALLOWED_HEADERS = "Content-Type";

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
    void ensureCorsHeadersArePresentIfOriginMatchesAndUsesHttp() {
        given()
                .header("Origin", VALID_ORIGIN)
                .when()
                .get()
                .then()
                .header("Access-Control-Allow-Origin", VALID_ORIGIN)
                .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
                .header("Access-Control-Allow-Headers", ALLOWED_HEADERS);
    }

    @Test
    void ensureCorsHeadersArePresentIfOriginMatchesAndUsesHttps() {
        given()
                .header("Origin", VALID_ORIGIN.replace("http", "https"))
                .when()
                .get()
                .then()
                .header("Access-Control-Allow-Origin", VALID_ORIGIN.replace("http", "https"))
                .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
                .header("Access-Control-Allow-Headers", ALLOWED_HEADERS);
    }

    @Test
    void ensureCorsHeadersAreNotPresentIfOriginDoesNotMatch() {
        given()
                .header("Origin", INVALID_ORIGIN)
                .when()
                .get()
                .then()
                .header("Access-Control-Allow-Origin", nullValue())
                .header("Access-Control-Allow-Methods", nullValue())
                .header("Access-Control-Allow-Headers", nullValue());
    }
}
