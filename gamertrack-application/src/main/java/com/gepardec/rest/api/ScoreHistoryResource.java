package com.gepardec.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.ScoreHistoryResource.BASE_SCOREHISTORY_PATH;


@Path(BASE_SCOREHISTORY_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ScoreHistoryResource {
    public static final String BASE_SCOREHISTORY_PATH = "scorehistory";
    public static final String TOKEN_PATH = "{token}";


    @Operation(summary = "Get all score history entries (optional filter: user, game & match)",
            description = "Returns list of score history entries ordered by creation time. "
                    + "History entries are write-once: they are created when a match is saved and cannot be modified or deleted via the API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    @GET()
    public Response getScoreHistory(@QueryParam("user") String userToken,
                                    @QueryParam("game") String gameToken,
                                    @QueryParam("match") String matchToken);

    @Operation(summary = "Get score history entry by token", description = "Returns a single score history entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "No Content - No score history entry was found")
    })
    @Path(TOKEN_PATH)
    @GET
    public Response getScoreHistoryByToken(@PathParam("token") String token);

}
