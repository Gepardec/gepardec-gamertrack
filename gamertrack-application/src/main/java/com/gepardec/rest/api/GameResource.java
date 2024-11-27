package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import jakarta.data.repository.Update;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GameResource {

  @GET
  Response getGames();

  @GET
  @Path("{id}")
  Response getGame(@PathParam("id") Long id);

  @POST
  Response createGame(@Valid CreateGameCommand gameCmd);

  @Update
  @Path("{id}")
  Response updateGame(@PathParam("id") Long id, UpdateGameCommand gameCmd);

  @DELETE
  @Path("{id}")
  Response deleteGame(@PathParam("id") Long id);
}
