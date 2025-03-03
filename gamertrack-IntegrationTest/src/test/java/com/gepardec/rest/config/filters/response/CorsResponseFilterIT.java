package com.gepardec.rest.config.filters.response;

import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.gepardec.rest.config.filters.response.CorsResponseFilter.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.nullValue;

public class CorsResponseFilterIT {

    private final String VALID_ORIGIN = "http://gamertrack-frontend.apps.cloudscale-lpg-2.appuio.cloud";
    private final String INVALID_ORIGIN = "http://lkadsjlksjdfgamertrack-frontend.apps.cloudscale-lpg-2.appuio.com";

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
                .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                .header("Access-Control-Allow-Credentials", ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED)
                .header("Access-Control-Expose-Headers", ACCESS_CONTROL_EXPOSE_HEADERS);
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
                .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                .header("Access-Control-Allow-Credentials", ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED)
                .header("Access-Control-Expose-Headers", ACCESS_CONTROL_EXPOSE_HEADERS);
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
                .header("Access-Control-Allow-Headers", nullValue())
                .header("Access-Control-Allow-Credentials", nullValue())
                .header("Access-Control-Expose-Headers", nullValue());
    }

    @Test
    void ensureCorsWorksWhenMakingAHeadRequest() {
        given()
                .header("Origin", VALID_ORIGIN)
                .when()
                .head()
                .then()
                .header("Access-Control-Allow-Origin", VALID_ORIGIN)
                .header("Access-Control-Allow-Methods", ALLOWED_METHODS)
                .header("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                .header("Access-Control-Allow-Credentials", ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED)
                .header("Access-Control-Expose-Headers", ACCESS_CONTROL_EXPOSE_HEADERS)
                .body(blankOrNullString());
    }
}
