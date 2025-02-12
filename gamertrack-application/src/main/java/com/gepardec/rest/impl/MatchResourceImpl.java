package com.gepardec.rest.impl;

import com.gepardec.core.services.MatchService;
import com.gepardec.rest.api.MatchResource;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.mapper.MatchRestMapper;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RequestScoped
public class MatchResourceImpl implements MatchResource {

    private final Logger logger = LoggerFactory.getLogger(MatchResourceImpl.class);

    @Inject
    private MatchService matchService;

    @Inject
    private MatchRestMapper restMapper;

    @Override
    public Response getMatches(Optional<String> gameToken, Optional<String> userToken, Optional<Long> pageNumber, Optional<Integer> pageSize) {

        if (gameToken.isPresent() || userToken.isPresent()) {
            return Response.ok()
                    .entity(matchService.findMatchesByGameTokenAndUserToken(gameToken, userToken, PageRequest.ofPage(pageNumber.orElse(1L), pageSize.orElse(10), true))
                            .stream()
                            .map(MatchRestDto::new)
                            .toList())
                    .build();
        }

        logger.info("Getting all existing Matches");
        return Response.ok()
                .entity(matchService.findAllMatches()
                        .stream()
                        .map(MatchRestDto::new)
                        .toList())
                .build();
    }


    @Override
    public Response getMatchByToken(String token) {
        logger.info("Getting match with ID: %s".formatted(token));

        return matchService.findMatchByToken(token)
                .map(MatchRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }

    @Override
    public Response createMatch(CreateMatchCommand matchCmd) {
        logger.info("Creating match: %s".formatted(matchCmd));
        return matchService.saveMatch(restMapper.createMatchCommandtoMatch(matchCmd))
                .map(MatchRestDto::new)
                .map(go -> Response.status(Status.CREATED).entity(go))
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response updateMatch(String token, UpdateMatchCommand matchCmd) {
        logger.info("Updating match with ID: %s".formatted(token));
        if (matchCmd == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        return matchService.updateMatch(restMapper.updateMatchCommandtoMatch(null, token, matchCmd))
                .map(MatchRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response deleteMatch(String token) {
        logger.info("Deleting match with Token: %s".formatted(token));
        return matchService.deleteMatch(token)
                .map(MatchRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }
}
