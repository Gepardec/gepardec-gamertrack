package com.gepardec.rest.api;

import com.gepardec.rest.config.Secure;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import io.swagger.annotations.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.validation.Valid;
import java.util.Optional;

@Path("matches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Matches", tags = {"matches"}) // Swagger 2 Klassengruppierung
public interface MatchResource {

    @GET
    @ApiOperation(
            value = "Gets all existing matches",
            notes = "Optional filters by gameToken or userToken; supports pagination via pageNumber/pageSize"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK")
    })
    Response getMatches(
            @ApiParam(value = "Filter by game token") @QueryParam("gameToken") Optional<String> gameToken,
            @ApiParam(value = "Filter by user token") @QueryParam("userToken") Optional<String> userToken,
            @ApiParam(value = "Page number (0-based)") @QueryParam("pageNumber") Optional<Long> pageNumber,
            @ApiParam(value = "Page size") @QueryParam("pageSize") Optional<Integer> pageSize
    );

    @GET
    @Path("{token}")
    @ApiOperation(
            value = "Gets match by token",
            notes = "Match must exist",
            response = GameRestDto.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 204, message = "Match not found")
    })
    Response getMatchByToken(
            @ApiParam(value = "Match token", required = true) @PathParam("token") String token
    );

    @POST
    @Secure
    @ApiOperation(
            value = "Creates a match",
            notes = "Match must be valid",
            response = GameRestDto.class
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Could not create match / Entity not valid")
    })
    Response createMatch(
            @ApiParam(value = "CreateMatch payload", required = true) @Valid CreateMatchCommand matchCmd
    );

    @PUT
    @Path("{token}")
    @Secure
    @ApiOperation(
            value = "Updates a match",
            notes = "Match must be valid and exist; referenced user/game ids must exist",
            response = GameRestDto.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Match has been updated"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    Response updateMatch(
            @ApiParam(value = "Match token", required = true) @PathParam("token") String token,
            @ApiParam(value = "UpdateMatch payload", required = true) @Valid UpdateMatchCommand matchCommand
    );

    @DELETE
    @Path("{token}")
    @Secure
    @ApiOperation(
            value = "Deletes a match",
            notes = "Match must exist",
            response = GameRestDto.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Match has been deleted"),
            @ApiResponse(code = 404, message = "Match not found")
    })
    Response deleteMatch(
            @ApiParam(value = "Match token", required = true) @PathParam("token") String token
    );
}
