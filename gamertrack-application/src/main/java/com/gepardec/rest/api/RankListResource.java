package com.gepardec.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.RankListResource.BASE_RANKLIST_PATH;


@Path(BASE_RANKLIST_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RankListResource {

    public static final String BASE_RANKLIST_PATH = "ranklist";
    public static final String ID_PATH = "{gameId}";

    @Operation(summary = "Get top scores by game (optional: TopCount)", description = "Returns a list of scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "No Content - No scores were found")
    })
    @Path(ID_PATH)
    @GET
    public Response getTopScoresBygame(@PathParam("gameId") Long gameId, @QueryParam("top") @DefaultValue("50") int top);

}
