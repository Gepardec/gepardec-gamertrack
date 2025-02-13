package com.gepardec.rest.impl;

import com.gepardec.core.services.MatchService;
import com.gepardec.model.Match;
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

import java.util.List;
import java.util.Optional;

import static java.lang.StrictMath.ceil;

@RequestScoped
public class MatchResourceImpl implements MatchResource {

    private final Logger logger = LoggerFactory.getLogger(MatchResourceImpl.class);

    @Inject
    private MatchService matchService;

    @Inject
    private MatchRestMapper restMapper;

    @Override
    public Response getMatches(Optional<String> gameToken, Optional<String> userToken, Optional<Long> pageNumber, Optional<Integer> pageSize) {
        PageRequest pageRequest = PageRequest.ofPage(pageNumber.orElse(1L), pageSize.orElse(Integer.MAX_VALUE), true);
        List<Match> matches = matchService.findAllMatches();

        if (gameToken.isPresent() || userToken.isPresent()) {
            List<Match> filteredMatches = matches.stream()
                    .filter(match -> gameToken
                            .map(token -> token.equals(match.getGame().getToken()))
                            .orElse(true))
                    .filter(match -> userToken
                            .map(token -> match.getUsers().stream().anyMatch(user -> token.equals(user.getToken())))
                            .orElse(true))
                    .toList();

            logger.info("Getting filtered matches by gameToken: %s and userToken: %s".formatted(gameToken, userToken));
            return createPaginatedResponse(
                    filteredMatches.size(),
                    pageRequest,
                    matchService.findMatchesByGameTokenAndUserToken(gameToken, userToken, pageRequest)
            );
        }

        logger.info("Getting all existing Matches");
        return createPaginatedResponse(matches.size(), pageRequest, matchService.findAllMatches(pageRequest));
    }

    public Response createPaginatedResponse(long totalData, PageRequest pageRequest, List<Match> bodyData) {
        return Response.ok()
                .header("X-Total-Count", totalData)
                .header("X-Total-Pages", (int) ceil((double) totalData / pageRequest.size()))
                .header("X-Page-Size", pageRequest.size())
                .header("X-Current-Page", pageRequest.page())
                .entity(bodyData.stream()
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
