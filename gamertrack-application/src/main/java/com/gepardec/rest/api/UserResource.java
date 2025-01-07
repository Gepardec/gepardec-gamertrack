package com.gepardec.rest.api;

import com.gepardec.rest.model.command.CreateUserCommand;
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

    @Operation(summary = "Updated user by token", description = "Returns the updated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The user was not found")
    })
    @Path("{token}")
    @PUT
    public Response updateUser(@PathParam("token") String token, @Valid UpdateUserCommand updateUserCommand);

    @Operation(summary = "Delete User by token", description = "Returns the deleted user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found - The user was not found")
    })
    @Path("{token}")
    @DELETE
    public Response deleteUser(@PathParam("token") String token);

    @Operation(summary = "Get all users", description = "Returns a list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "204", description = "No Content - No users were found")
    })    @GET
    public Response getUsers(@QueryParam("includeDeactivated") @DefaultValue(value = "true") Boolean includeDeactivated);

    @Operation(summary = "Get User by token", description = "Returns user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @Path("{token}")
    @GET
    public Response getUser(@PathParam("token") String token);




}
