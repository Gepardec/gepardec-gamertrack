package com.gepardec.rest.impl;

import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import com.gepardec.rest.api.UserResource;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import com.gepardec.rest.model.dto.UserDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
public class UserResourceImpl implements UserResource {
    @Inject
    private UserService userService;

    @Override
    public Response createUser(CreateUserCommand userCommand) {
        return userService.saveUser(new User(userCommand.firstname(),userCommand.lastname())).map(UserDto::new)
                .map(userDto -> Response.status(Status.CREATED).entity(userDto))
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();

    }

    @Override
    public Response updateUser(Long id, UpdateUserCommand updateUserCommand){
        return userService.updateUser(id, new User(updateUserCommand.firstname(),updateUserCommand.lastname())).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();
    }


    @Override
    public Response getUser(Long id){
        return userService.findUserById(id).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NO_CONTENT)).build();
    }


    @Override
    public Response getUsers(){
        return userService.findAllUsers().stream().map(UserDto::new).toList().isEmpty()
                ? Response.status(Status.NO_CONTENT).build()
                : Response.ok().entity(userService.findAllUsers().stream().map(UserDto::new).toList()).build();

    }

    @Override
    public Response getUsersIncludeDeleted() {
        return userService.findAllUsersIncludeDeleted().stream().map(UserDto::new).toList().isEmpty()
                ? Response.status(Status.NO_CONTENT).build()
                : Response.ok().entity(userService.findAllUsersIncludeDeleted().stream().map(UserDto::new).toList()).build();
    }

    @Override
    public Response deleteUser(Long id){
        return userService.deleteUser(id).map(UserDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Status.NOT_FOUND)).build();
    }

}
