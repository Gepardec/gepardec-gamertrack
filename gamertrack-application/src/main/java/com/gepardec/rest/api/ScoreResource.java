package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.UserResource.BASE_PATH;

@Path(BASE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ScoreResource {
    public static final String BASE_PATH = "scores";
    public static final String ID_PATH = "{id}";
    public static final String USER_ID_PATH = "user/"+ ID_PATH;
    public static final String GAME_ID_PATH = "game/"+ ID_PATH;
    public static final String SCOREPOINTS_PATH = "scorepoints/{points}";

    @GET()
    public Response getScores();

    @Path(ID_PATH)
    @GET
    public Response getScoreById(@PathParam("id") Long id);

    @Path(USER_ID_PATH )
    @GET
    public Response getScoreByUser(@PathParam("id") Long id);

    @Path(GAME_ID_PATH + "{id}")
    @GET
    public Response getScoreByGame(@PathParam("id") Long gameId);

    @Path( SCOREPOINTS_PATH)
    @GET
    public Response getScoreByScorePoints(@PathParam("points") double points);
}
