package com.gepardec.rest.api;

import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.UserResource.BASE_PATH;

@Path(BASE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserResource {

    public static final String BASE_PATH = "users";

    @GET
    public Response getUsers();

    @Path("{id}")
    @GET
    public Response getUser(@PathParam("id") Long id);

    @POST
    Response createUser(CreateUserCommand userCommand);

    @Path("{id}")
    @PUT
    public Response updateUser(@PathParam("id") Long id, UpdateUserCommand updateUserCommand);

    @Path("{id}")
    @DELETE
    public Response deleteUser(@PathParam("id") Long id);


}
