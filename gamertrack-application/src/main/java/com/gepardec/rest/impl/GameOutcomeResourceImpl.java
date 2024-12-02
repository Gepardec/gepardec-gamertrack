package com.gepardec.rest.impl;

import com.gepardec.interfaces.services.GameOutcomeService;
import com.gepardec.rest.api.GameOutcomeResource;
import com.gepardec.rest.model.command.CreateGameOutcomeCommand;
import com.gepardec.rest.model.command.UpdateGameOutcomeCommand;
import com.gepardec.rest.model.dto.GameOutcomeDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class GameOutcomeResourceImpl implements GameOutcomeResource {

  private final Logger logger = LoggerFactory.getLogger(GameOutcomeResourceImpl.class);

  @Inject
  private GameOutcomeService gameOutcomeService;

  @Override
  public Response getGameOutcomes(Optional<Long> gameId, Optional<Long> userId) {

    if (gameId.isPresent()) {
      logger.info("Getting all GameOutcomes with GameID: %s".formatted(gameId.get()));
      return Response.ok()
          .entity(gameOutcomeService.findGameOutcomesByGameId(gameId.get())
              .stream()
              .map(GameOutcomeDto::new)
              .toList())
          .build();
    }

    if (userId.isPresent()) {
      logger.info("Getting all GameOutcomes with UserID: %s".formatted(userId.get()));
      return Response.ok().
          entity(gameOutcomeService.findGameOutcomeByUserId(userId.get())
              .stream().map(GameOutcomeDto::new)
              .toList())
          .build();
    }

    logger.info("Getting all existing GameOutcomes");
    return Response.ok().entity(gameOutcomeService.findAllGameOutcomes().stream()
        .map(GameOutcomeDto::new).toList()).build();
  }


  @Override
  public Response getGameOutcomeById(Long id) {
    logger.info("Getting GameOutcome with ID: %s".formatted(id));
    return Response.ok()
        .entity(gameOutcomeService.findGameOutcomeById(id).map(GameOutcomeDto::new))
        .build();

  }

  @Override
  public Response createGameOutcome(CreateGameOutcomeCommand gameOutcomeCmd) {
    logger.info("Creating GameOutcome: %s".formatted(gameOutcomeCmd));
    return gameOutcomeService.saveGameOutcome(gameOutcomeCmd.gameId(), gameOutcomeCmd.userIds())
        .map(GameOutcomeDto::new)
        .map(go -> Response.status(Status.CREATED).entity(go))
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
  }

  @Override
  public Response updateGameOutcome(Long id, UpdateGameOutcomeCommand gameOutcomeCommand) {
    logger.info("Updating GameOutcome with ID: %s".formatted(id));
    if (gameOutcomeCommand == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    return gameOutcomeService.updateGameOutcome(id,
            gameOutcomeCommand.gameId(), gameOutcomeCommand.userIds())
        .map(GameOutcomeDto::new)
        .map(Response::ok)
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
  }

  @Override
  public Response deleteGameOutcome(Long id) {
    logger.info("Deleting GameOutcome with ID: %s".formatted(id));
    return Response.ok(gameOutcomeService.deleteGameOutcome(id)).build();
  }
}
