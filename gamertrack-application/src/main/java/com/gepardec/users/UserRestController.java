package com.gepardec.users;

import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import com.gepardec.users.cmds.CreateUserCommand;
import com.gepardec.users.dto.UserDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRestController {
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
