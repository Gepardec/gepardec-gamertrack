package com.gepardec.rest.impl;

import com.gepardec.core.services.MatchService;
import com.gepardec.rest.api.MatchResource;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.mapper.RestMapper;
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
  private RestMapper restMapper;

  @Override
  public Response getMatches(Optional<Long> gameId, Optional<Long> userId) {

    if (gameId.isPresent() || userId.isPresent()) {
      return Response.ok()
          .entity(matchService.findMatchesByUserIdAndGameId(userId, gameId)
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
  public Response getMatchById(Long id) {
    logger.info("Getting match with ID: %s".formatted(id));

    return matchService.findMatchById(id)
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
  public Response updateMatch(Long id, UpdateMatchCommand matchCommand) {
    logger.info("Updating match with ID: %s".formatted(id));
    if (matchCommand == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }

    return matchService.updateMatch(restMapper.updateMatchCommandtoMatch(id, matchCommand))
        .map(MatchRestDto::new)
        .map(Response::ok)
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
  }

  @Override
  public Response deleteMatch(Long id) {
    logger.info("Deleting match with ID: %s".formatted(id));
    return matchService.deleteMatch(id)
        .map(MatchRestDto::new)
        .map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }
}
