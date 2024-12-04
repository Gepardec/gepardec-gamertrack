package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateGameOutcomeCommand;
import com.gepardec.rest.model.command.UpdateGameOutcomeCommand;
import com.gepardec.rest.model.dto.GameDto;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("gameoutcomes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GameOutcomeResource {


  @Operation(summary = "Gets all existing gameoutcomes from the database, or all Gameoutcomes filtered either by gameID or userID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok")})

  @GET
  Response getGameOutcomes(@QueryParam("gameId") Optional<Long> gameId,
      @QueryParam("userId") Optional<Long> userId);


  @Operation(summary = "Gets gameoutcome by ID", description = "Gameoutcome must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameDto.class))}),
      @ApiResponse(responseCode = "204", description = "Gameoutcome not found")})

  @GET
  @Path("{id}")
  Response getGameOutcomeById(@PathParam("id") Long id);


  @Operation(summary = "Creates a gameoutcome", description = "Gameoutcome must be valid")
  @RequestBody(content = @Content(schema = @Schema(implementation = CreateGameOutcomeCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameDto.class))}),
      @ApiResponse(responseCode = "400", description = "Could not create gameoutcome/Entity was not valid")
  })

  @POST
  Response createGameOutcome(CreateGameOutcomeCommand gameOutcomeCmd);


  @Operation(summary = "Updates a gameoutcome", description = "Gameoutcome must be valid and exist, specified user and game ids that make up the gameoutcome have to exist")
  @RequestBody(content = @Content(schema = @Schema(implementation = UpdateGameOutcomeCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Gameoutcome has been updated", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameDto.class))}),
      @ApiResponse(responseCode = "400", description = "Bad Request")
  })

  @PUT
  @Path("{id}")
  Response updateGameOutcome(@PathParam("id") Long id,
      @Valid UpdateGameOutcomeCommand gameOutcomeCommand);


  @Operation(summary = "Deletes a gameoutcome", description = "Gameoutcome must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Gameoutcome has been deleted", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameDto.class))}),
      @ApiResponse(responseCode = "404", description = "Gameoutcome not found")})

  @DELETE
  @Path("{id}")
  Response deleteGameOutcome(@PathParam("id") Long id);
}
