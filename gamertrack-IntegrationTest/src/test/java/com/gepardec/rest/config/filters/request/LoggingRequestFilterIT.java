package com.gepardec.rest.config.filters.request;

import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.*;

class LoggingRequestFilterIT {

    private final MemoryAppender memoryAppender = new MemoryAppender();

    @BeforeAll
    public static void setup() {
        reset();
        port = 8080;
        basePath = "gepardec-gamertrack/api/v1/games";
        enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    @BeforeEach
    public void before() {

        Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);


    }

    @AfterAll
    public static void cleanup() {
        reset();
    }
}