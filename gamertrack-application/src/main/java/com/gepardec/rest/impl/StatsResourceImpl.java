package com.gepardec.rest.impl;

import com.gepardec.core.services.StatisticsService;
import com.gepardec.rest.api.StatsResource;
import com.gepardec.rest.model.dto.ErrorRestDto;
import com.gepardec.rest.model.dto.HeadToHeadRestDto;
import com.gepardec.rest.model.dto.PlayerFormRestDto;
import com.gepardec.rest.model.dto.PlayerGameStatsRestDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class StatsResourceImpl implements StatsResource {

    private final Logger logger = LoggerFactory.getLogger(StatsResourceImpl.class);

    @Inject
    private StatisticsService statisticsService;

    @Override
    public Response getPlayerGameStats(String userToken, String gameToken) {
        logger.info("Getting stats for userToken: %s and gameToken: %s".formatted(userToken, gameToken));

        return statisticsService.getPlayerGameStats(userToken, gameToken)
                .map(PlayerGameStatsRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> unknownUserOrGame())
                .build();
    }

    @Override
    public Response getPlayerForm(String userToken, String gameToken, int limit) {
        logger.info("Getting form for userToken: %s and gameToken: %s with limit: %s"
                .formatted(userToken, gameToken, limit));

        if (limit <= 0) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ErrorRestDto("limit must be greater than 0"))
                    .build();
        }

        return statisticsService.getPlayerForm(userToken, gameToken, Math.min(limit, MAX_FORM_RESULTS))
                .map(PlayerFormRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> unknownUserOrGame())
                .build();
    }

    @Override
    public Response getHeadToHead(String firstUserToken, String secondUserToken, String gameToken) {
        logger.info("Getting head-to-head for userTokens: %s and %s and gameToken: %s"
                .formatted(firstUserToken, secondUserToken, gameToken));

        if (firstUserToken == null || firstUserToken.isBlank()
                || secondUserToken == null || secondUserToken.isBlank()
                || gameToken == null || gameToken.isBlank()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ErrorRestDto(
                            "firstUserToken, secondUserToken and gameToken must be provided"))
                    .build();
        }

        if (firstUserToken.equals(secondUserToken)) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new ErrorRestDto("firstUserToken and secondUserToken must be different"))
                    .build();
        }

        return statisticsService.getHeadToHead(firstUserToken, secondUserToken, gameToken)
                .map(HeadToHeadRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> unknownUserOrGame())
                .build();
    }

    private static Response.ResponseBuilder unknownUserOrGame() {
        return Response.status(Status.NOT_FOUND)
                .entity(new ErrorRestDto("User or game not found"));
    }
}
