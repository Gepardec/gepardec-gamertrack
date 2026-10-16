package com.gepardec.rest.api;

import com.gepardec.rest.config.Secure;
import com.gepardec.rest.model.dto.HeadToHeadRestDto;
import com.gepardec.rest.model.dto.PlayerFormRestDto;
import com.gepardec.rest.model.dto.PlayerGameStatsRestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.StatsResource.BASE_STATS_PATH;

@Path(BASE_STATS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface StatsResource {

    String BASE_STATS_PATH = "stats";
    int DEFAULT_FORM_RESULTS = 10;
    int MAX_FORM_RESULTS = 50;

    @Operation(summary = "Gets aggregated statistics of a player for one game",
            description = "Returns matches played, wins, draws, losses, win rate, current streak and "
                    + "longest win streak, computed from the stored matches of the given game. Matches "
                    + "without a stored result are excluded and reported via excludedMatches. A player "
                    + "who never played the game gets zeroed stats.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema =
                    @Schema(implementation = PlayerGameStatsRestDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or game not found")})
    @GET
    @Path("players/{userToken}/games/{gameToken}")
    @Secure
    Response getPlayerGameStats(@PathParam("userToken") String userToken,
                                @PathParam("gameToken") String gameToken);

    @Operation(summary = "Gets the recent form of a player for one game",
            description = "Returns the results of the player's last matches of the given game, newest "
                    + "first. The number of results can be limited with the limit query parameter "
                    + "(default " + DEFAULT_FORM_RESULTS + ", at most " + MAX_FORM_RESULTS + "; larger "
                    + "values are capped). Matches without a stored result are excluded and reported "
                    + "via excludedMatches.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema =
                    @Schema(implementation = PlayerFormRestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Limit is zero or negative"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or game not found")})
    @GET
    @Path("players/{userToken}/games/{gameToken}/form")
    @Secure
    Response getPlayerForm(@PathParam("userToken") String userToken,
                           @PathParam("gameToken") String gameToken,
                           @QueryParam("limit") @DefaultValue("" + DEFAULT_FORM_RESULTS) int limit);

    @Operation(summary = "Gets the head-to-head record of two players for one game",
            description = "Returns the mutual record of the two players based only on stored matches of "
                    + "the given game both players participated in. In multiplayer matches the better "
                    + "placement counts as the head-to-head win, an equal placement counts as a draw. "
                    + "Asking with swapped user tokens yields the mirrored result. Players who never "
                    + "met get a zeroed record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON, schema =
                    @Schema(implementation = HeadToHeadRestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Missing or equal user tokens"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or game not found")})
    @GET
    @Path("head-to-head")
    @Secure
    Response getHeadToHead(@QueryParam("firstUserToken") String firstUserToken,
                           @QueryParam("secondUserToken") String secondUserToken,
                           @QueryParam("gameToken") String gameToken);
}
