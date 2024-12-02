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

@RequestScoped
public class GameOutcomeResourceImpl implements GameOutcomeResource {

  @Inject
  private GameOutcomeService gameOutcomeService;

  @Override
  public Response getGameOutcomes(Optional<Long> gameId, Optional<Long> userId) {
    System.out.println("executing getGameOutcomes Controller");
    if (gameId.isPresent()) {
      System.out.println("gameId: " + gameId);
      return Response.ok()
          .entity(gameOutcomeService.findGameOutcomesByGameId(gameId.get())
              .stream()
              .map(GameOutcomeDto::new)
              .toList())
          .build();
    }

    if (userId.isPresent()) {
      System.out.println("userId: " + userId);
      return Response.ok().
          entity(gameOutcomeService.findGameOutcomeByUserId(userId.get())
              .stream().map(GameOutcomeDto::new)
              .toList())
          .build();
    }

    return Response.ok().entity(gameOutcomeService.findAllGameOutcomes().stream()
        .map(GameOutcomeDto::new).toList()).build();
  }


  @Override
  public Response getGameOutcomeById(Long id) {
    return Response.ok()
        .entity(gameOutcomeService.findGameOutcomeById(id).map(GameOutcomeDto::new))
        .build();

  }

  @Override
  public Response createGameOutcome(CreateGameOutcomeCommand gameOutcomeCmd) {
    return gameOutcomeService.saveGameOutcome(gameOutcomeCmd.gameId(), gameOutcomeCmd.userIds())
        .map(GameOutcomeDto::new)
        .map(go -> Response.status(Status.CREATED).entity(go))
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
  }

  @Override
  public Response updateGameOutcome(Long id, UpdateGameOutcomeCommand gameOutcomeCommand) {
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
    return Response.ok(gameOutcomeService.deleteGameOutcome(id)).build();
  }
}
