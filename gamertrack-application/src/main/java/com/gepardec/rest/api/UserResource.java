package com.gepardec.rest.api;

import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.UserResource.BASE_USER_PATH;

@Path(BASE_USER_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserResource {

    public static final String BASE_USER_PATH = "users";

    @Operation(summary = "Post a user", description = "Returns the created user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Created - The user was not created")
    })
    @POST
    Response createUser(@Valid CreateUserCommand userCommand);

    @Operation(summary = "Updated user by id", description = "Returns the updated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The user was not found")
    })
    @Path("{id}")
    @PUT
    public Response updateUser(@PathParam("id") Long id, @Valid UpdateUserCommand updateUserCommand);

    @Operation(summary = "Delete User by id", description = "Returns the deleted user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The user was not found")
    })
    @Path("{id}")
    @DELETE
    public Response deleteUser(@PathParam("id") Long id);

    @Operation(summary = "Get all users", description = "Returns a list of users")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved"))
    @GET
    public Response getUsers();

    @Operation(summary = "Get all users including the deleted User", description = "Returns a list of users including")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successfully retrieved"))
    @Path("/includeDeleted")
    @GET
    public Response getUsersIncludeDeleted();

    @Operation(summary = "Get User by id", description = "Returns user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The user was not found")
    })
    @Path("{id}")
    @GET
    public Response getUser(@PathParam("id") Long id);




}
