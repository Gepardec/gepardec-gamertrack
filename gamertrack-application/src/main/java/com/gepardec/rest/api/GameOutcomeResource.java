package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateGameOutcomeCommand;
import com.gepardec.rest.model.command.UpdateGameOutcomeCommand;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("gameoutcomes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GameOutcomeResource {

  @GET
  Response getGameOutcomes(@QueryParam("gameId") Optional<Long> gameId, @QueryParam("userId") Optional<Long> userId);

  @GET
  @Path("{id}")
  Response getGameOutcomeById(@PathParam("id") Long id);

  @POST
  Response createGameOutcome(CreateGameOutcomeCommand gameOutcomeCmd);

  @PUT
  @Path("{id}")
  Response updateGameOutcome(@PathParam("id") Long id, @Valid UpdateGameOutcomeCommand gameOutcomeCommand);

  @DELETE
  @Path("{id}")
  Response deleteGameOutcome(@PathParam("id") Long id);
}
