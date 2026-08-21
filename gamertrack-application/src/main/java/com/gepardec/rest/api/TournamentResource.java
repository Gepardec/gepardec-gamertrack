package com.gepardec.rest.api;

import com.gepardec.rest.config.Secure;
import com.gepardec.rest.model.command.AddTournamentParticipantCommand;
import com.gepardec.rest.model.command.CreateTournamentCommand;
import com.gepardec.rest.model.command.CreateTournamentMatchCommand;
import com.gepardec.rest.model.command.UpdateTournamentStateCommand;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.TournamentRestDto;
import com.gepardec.rest.model.dto.TournamentStandingRestDto;
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

@Path("tournaments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TournamentResource {


  @Operation(summary = "Gets all existing tournaments from the database")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok")})

  @GET
  Response getTournaments();


  @Operation(summary = "Gets tournament by token", description = "Tournament must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "404", description = "Tournament not found")})

  @GET
  @Path("{token}")
  Response getTournamentByToken(@PathParam("token") String token);


  @Operation(summary = "Creates a tournament",
      description = "Requires a name, an existing game and at least two distinct existing users as participants. The tournament starts in state CREATED")
  @RequestBody(content = @Content(schema = @Schema(implementation = CreateTournamentCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Could not create tournament/Entity was not valid")
  })

  @POST
  @Secure
  Response createTournament(@Valid CreateTournamentCommand tournamentCmd);


  @Operation(summary = "Moves a tournament to a new state",
      description = "The state only moves forward: CREATED -> RUNNING -> DONE. Backward transitions are rejected")
  @RequestBody(content = @Content(schema = @Schema(implementation = UpdateTournamentStateCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tournament state has been updated", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Tournament does not exist or transition is not allowed")
  })

  @PUT
  @Path("{token}/state")
  @Secure
  Response updateTournamentState(@PathParam("token") String token,
      @Valid UpdateTournamentStateCommand stateCmd);


  @Operation(summary = "Adds a participant to a tournament",
      description = "Only allowed while the tournament is in state CREATED; the user must exist and must not already participate")
  @RequestBody(content = @Content(schema = @Schema(implementation = AddTournamentParticipantCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Participant has been added", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Tournament/user does not exist, tournament already started or user already participates")
  })

  @POST
  @Path("{token}/participants")
  @Secure
  Response addParticipant(@PathParam("token") String token,
      @Valid AddTournamentParticipantCommand participantCmd);


  @Operation(summary = "Removes a participant from a tournament",
      description = "Only allowed while the tournament is in state CREATED; at least two participants must remain")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Participant has been removed", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Tournament does not exist, tournament already started, user does not participate or too few participants would remain")
  })

  @DELETE
  @Path("{token}/participants/{userToken}")
  @Secure
  Response removeParticipant(@PathParam("token") String token,
      @PathParam("userToken") String userToken);


  @Operation(summary = "Plays a match inside a tournament",
      description = "Only allowed while the tournament is RUNNING and all users are registered participants. "
          + "The match is a normal match of the tournament's game: users are given in result order (first user is the winner) and rating changes apply as usual")
  @RequestBody(content = @Content(schema = @Schema(implementation = CreateTournamentMatchCommand.class)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = MatchRestDto.class))}),
      @ApiResponse(responseCode = "400", description = "Tournament does not exist, is not running or the users are no registered participants")
  })

  @POST
  @Path("{token}/matches")
  @Secure
  Response createTournamentMatch(@PathParam("token") String token,
      @Valid CreateTournamentMatchCommand matchCmd);


  @Operation(summary = "Gets all matches played inside a tournament", description = "Tournament must exist")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok"),
      @ApiResponse(responseCode = "404", description = "Tournament not found")})

  @GET
  @Path("{token}/matches")
  Response getTournamentMatches(@PathParam("token") String token);


  @Operation(summary = "Gets the standings of a tournament",
      description = "Lists every participant ordered by wins (descending), then matches played (descending), then lastname, firstname and user token. "
          + "A win means finishing first in a tournament match")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentStandingRestDto.class))}),
      @ApiResponse(responseCode = "404", description = "Tournament not found")})

  @GET
  @Path("{token}/standings")
  Response getTournamentStandings(@PathParam("token") String token);


  @Operation(summary = "Deletes a tournament",
      description = "Allowed in any state. The played matches, their rating changes, users and the game are kept")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tournament has been deleted", content = {
          @Content(mediaType = MediaType.APPLICATION_JSON, schema =
          @Schema(implementation = TournamentRestDto.class))}),
      @ApiResponse(responseCode = "404", description = "Tournament not found")})

  @DELETE
  @Path("{token}")
  @Secure
  Response deleteTournament(@PathParam("token") String token);
}
