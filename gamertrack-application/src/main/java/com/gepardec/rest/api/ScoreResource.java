package com.gepardec.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.ScoreResource.BASE_SCORE_PATH;


@Path(BASE_SCORE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ScoreResource {
    public static final String BASE_SCORE_PATH = "scores";
    public static final String ID_PATH = "{id}";
    public static final String SCOREPOINTS_PATH = "scorepoints/{points}";


    @Operation(summary = "Get all Scores (optional filter: min, max, user & game)", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "No Content - No scores were found")
    })
    @GET()
    public Response getScores(@QueryParam("min") Double minScore,
                              @QueryParam("max") Double maxScore,
                              @QueryParam("user") String userToken,
                              @QueryParam("game") String gameToken,
                              @QueryParam("includeDeactivated") Boolean includeDeactivated);

    @Operation(summary = "Get Scores by id", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Path(ID_PATH)
    @GET
    public Response getScoreByToken(@PathParam("id") String token);

    @Operation(summary = "Get Scores by userId", description = "Returns list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Path( SCOREPOINTS_PATH)
    @GET
    public Response getScoreByScorePoints(@PathParam("points") double points, @QueryParam("includeDeactivated") Boolean includeDeactivated);

}