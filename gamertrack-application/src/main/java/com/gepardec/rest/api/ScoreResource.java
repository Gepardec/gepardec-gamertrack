package com.gepardec.rest.api;

import com.gepardec.model.Score;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

import static com.gepardec.rest.api.ScoreResource.BASE_SCORE_PATH;


@Path(BASE_SCORE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ScoreResource {
    public static final String BASE_SCORE_PATH = "scores";
    public static final String ID_PATH = "{id}";
    public static final String USER_ID_PATH = "user/"+ ID_PATH;
    public static final String GAME_ID_PATH = "game/"+ ID_PATH;
    public static final String SCOREPOINTS_PATH = "scorepoints/{points}";

    //Only temporary for testing
    //-------------------------------
    @Operation(summary = "Post a score", description = "Returns the created score")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Created - The score was not created")
    })
    @POST
    public Response createScore(@Valid CreateScoreCommand createScoreCommand);
    //-------------------------------

    @Operation(summary = "Get all Scores (optional filter: min, max, user & game)", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "No Content - No scores were found")
    })
    @GET()
    public Response getScores(@QueryParam("min") Double minScore,
                              @QueryParam("max") Double maxScore,
                              @QueryParam("user") Long userId,
                              @QueryParam("game") Long gameId);

    @Operation(summary = "Get Scores by id", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Path(ID_PATH)
    @GET
    public Response getScoreById(@PathParam("id") Long id);

    @Operation(summary = "Get Scores by userId", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Path( SCOREPOINTS_PATH)
    @GET
    public Response getScoreByScorePoints(@PathParam("points") double points);

}