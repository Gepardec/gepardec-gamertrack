package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("matches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface MatchResource {


  @Operation(summary = "Gets all existing matches from the database, or all Matches filtered either by gameID or userID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok")})

  @GET
  Response getMatches(@QueryParam("gameId") Optional<Long> gameId,
      @QueryParam("userId") Optional<Long> userId);


  @Operation(summary = "Gets match by token", description = "Match must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))}),
      @ApiResponse(responseCode = "204", description = "Match not found")})

  @GET
  @Path("{token}")
  Response getMatchByToken(@PathParam("token") String token);


  @Operation(summary = "Creates a match", description = "Match must be valid")
  @RequestBody(content = @Content(schema = @Schema(implementation = CreateMatchCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Could not create match/Entity was not valid")
  })

  @POST
  Response createMatch(CreateMatchCommand matchCmd);


  @Operation(summary = "Updates a match", description = "Match must be valid and exist, specified user and game ids that make up the match have to exist")
  @RequestBody(content = @Content(schema = @Schema(implementation = UpdateMatchCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Match has been updated", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Bad Request")
  })

  @PUT
  @Path("{token}")
  Response updateMatch(@PathParam("token") String token,
      @Valid UpdateMatchCommand matchCommand);


  @Operation(summary = "Deletes a match", description = "Match must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Match has been deleted", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = GameRestDto.class))}),
      @ApiResponse(responseCode = "404", description = "Match not found")})

  @DELETE
  @Path("{id}")
  Response deleteMatch(@PathParam("id") Long id);
}
