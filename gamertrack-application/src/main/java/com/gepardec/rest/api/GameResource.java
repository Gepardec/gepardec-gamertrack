package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("games")
public interface GameResource {

  @Operation(summary = "Gets all existing games from the database")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok")})

  @GET
  Response getGames();


  @Operation(summary = "Gets game by token", description = "Game must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))}),
      @ApiResponse(responseCode = "404", description = "Game not found")})

  @GET
  @Path("{token}")
  Response getGame(@PathParam("token") String token);


  @Operation(summary = "Create a game", description = "Game Objekt must be Valid")
  @RequestBody(content = @Content(schema = @Schema(implementation = CreateGameCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Game successfully created", content =
      @Content(mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = GameRestDto.class))),
      @ApiResponse(responseCode = "400", description = "Could not create Game")})

  @POST
  Response createGame(@Valid CreateGameCommand gameCmd);


  @Operation(summary = "Update a game by token", description = "Game has to exist already")
  @RequestBody(content = @Content(schema = @Schema(implementation = UpdateGameCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Game has been updated successfully",
          content = @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))),
      @ApiResponse(responseCode = "404", description = "Game not found",
          content = @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class)))})

  @PUT
  @Path("{token}")
  Response updateGame(@PathParam("token") String token, UpdateGameCommand gameCmd);


  @Operation(summary = "Delete a game by ID ", description = "Game has to exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Game has been deleted successfully",
          content = @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))),
      @ApiResponse(responseCode = "404", description = "Game not found")})

  @DELETE
  @Path("{token}")
  Response deleteGame(@PathParam("token") String token);
}
