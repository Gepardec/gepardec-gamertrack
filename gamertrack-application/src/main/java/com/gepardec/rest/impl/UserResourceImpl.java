package com.gepardec.rest.impl;

import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.dto.UserDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResourceImpl {
    @Inject
    private UserService userService;

    @Path("new")
    @POST
    public Response createUser(CreateUserCommand userCommand) {

        return userService.saveUser(new User(userCommand.getFirstname(),userCommand.getLastname())).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();

    }


    @Path("{id}")
    @PUT
    public Response updateUser(@PathParam("id") Long id, User user){
        return userService.updateUser(id, user).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();
    }


    @Path("{id}")
    @GET
    public Response getUser(@PathParam("id") Long id){
        return userService.findUserById(id).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();
    }


    @Path("list")
    @GET
    public Response getUsers(){
        return Response.ok().entity(userService.findAllUsers().stream().map(UserDto::new).toList()).build();
    }

    @Path("{id}")
    @DELETE
    public Response deleteUser(@PathParam("id") Long id){
        userService.deleteUser(id);
        return Response.ok().build();
    }

}
