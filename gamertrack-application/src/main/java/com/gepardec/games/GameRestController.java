package com.gepardec.games;

import static com.gepardec.games.GameRestController.BASE_PATH;

import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Optional;
import org.jboss.jdeparser.FormatPreferences.Opt;

@Path(BASE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameRestController {

  public static final String BASE_PATH = "games";

  @Inject
  private GameService gameService;

  @GET
  public Response getGames() {
    //Return 200 with empty list with no games if none are in the db instead of other Statuscodes
    return Response.ok().entity(gameService.findAll().stream().map(GameDto::new).toList()).build();
  }

  @GET
  @Path("{id}")
  public Response getGame(@PathParam("id") int id) {
    //Return 200 ok with found game or not found if it does not exist
    return gameService.findGameById(id).map(GameDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }

  @POST
  public Response createGame(@Valid CreateGameCommand gameCmd) {
    //Return saved Game if it was persisted or Else return Bad Request
    return gameService.saveGame(new Game(gameCmd.title(), gameCmd.rules())).map(GameDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();

  }

  @PUT
  @Path("{id}")
  public Response updateGame(@PathParam("id") Long id, UpdateGameCommand gameCmd) {
    Optional<Game> gameOld = gameService.findGameById(id);

    if (gameOld.isPresent()) {
      Game game = gameOld.get();
      game.setName(gameCmd.title());
      game.setRules(gameCmd.rules());
      return Response.ok().entity(gameService.saveGame(game)).build();
    }

    return Response.status(Status.NOT_FOUND).build();
  }

  @DELETE
  @Path("{id}")
  public Response deleteGame(@PathParam("id") Long id) {
    gameService.deleteGame(id);
    return Response.status(Status.OK).build();
  }
}
