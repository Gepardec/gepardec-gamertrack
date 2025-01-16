package com.gepardec.rest.impl;

import com.gepardec.core.services.GameService;
import com.gepardec.model.Game;
import com.gepardec.rest.api.GameResource;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.mapper.RestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Optional;

@RequestScoped
public class GameResourceImpl implements GameResource {

  @Inject
  private GameService gameService;

  @Inject
  private RestMapper restMapper;

  @Override
  public Response getGames() {
    //Return 200 with empty list with no games if none are in the db instead of other Statuscodes
    return Response.ok().entity(gameService.findAllGames().stream().map(GameRestDto::new).toList())
        .build();
  }

  @Override
  public Response getGame(Long id) {
    //Return 200 ok with found game or not found if it does not exist
    return gameService.findGameById(id)
        .map(GameRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND))
        .build();
  }

  @Override
  public Response createGame(CreateGameCommand gameCmd) {
    //Return saved Game if it was persisted or Else return Bad Request
    return gameService.saveGame(restMapper.createGameCommandtoGame(gameCmd))
        .map(GameRestDto::new)
        .map(gameDto -> Response.status(Status.CREATED).entity(gameDto))
        .orElseGet(() -> Response.status(Status.BAD_REQUEST))
        .build();
  }

  @Override
  public Response updateGame(Long id, UpdateGameCommand gameCmd) {
    Optional<Game> gameOld = gameService.findGameById(id);

    //Returns updated 200OK with updated Entity if it exists or if it does not exist the same object provided on request
    return gameService.updateGame(restMapper.updateGameCommandtoGame(id, gameCmd))
        .map(GameRestDto::new)
        .map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND).entity(gameCmd))
        .build();
  }

  @Override
  public Response deleteGame(Long id) {
    return gameService.deleteGame(id).map(GameRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }
}
