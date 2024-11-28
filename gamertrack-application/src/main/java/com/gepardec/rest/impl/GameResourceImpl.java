package com.gepardec.rest.impl;

import static com.gepardec.rest.impl.GameResourceImpl.BASE_PATH;

import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import com.gepardec.rest.api.GameResource;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Optional;

@RequestScoped
public class GameResourceImpl implements GameResource {

  public static final String BASE_PATH = "games";

  @Inject
  private GameService gameService;

  @Override
  public Response getGames() {
    //Return 200 with empty list with no games if none are in the db instead of other Statuscodes
    return Response.ok().entity(gameService.findAll().stream().map(GameDto::new).toList()).build();
  }

  @Override
  public Response getGame(Long id) {
    //Return 200 ok with found game or not found if it does not exist
    return gameService.findGameById(id).map(GameDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }

  @Override
  public Response createGame(CreateGameCommand gameCmd) {
    //Return saved Game if it was persisted or Else return Bad Request
    return gameService.saveGame(new Game(gameCmd.title(), gameCmd.rules())).map(GameDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();

  }

  @Override
  public Response updateGame(Long id, UpdateGameCommand gameCmd) {
    Optional<Game> gameOld = gameService.findGameById(id);

    //Returns updated 200OK with updated Entity if it exists or if it does not exist the same object provided on request
    return gameService.updateGame(id, new Game(gameCmd.title(), gameCmd.rules()))
        .map(GameDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND).entity(gameCmd)).build();
  }

  @Override
  @DELETE
  public Response deleteGame(Long id) {
    gameService.deleteGame(id);
    return Response.status(Status.OK).build();
  }
}
