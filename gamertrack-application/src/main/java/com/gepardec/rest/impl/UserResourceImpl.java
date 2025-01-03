package com.gepardec.rest.impl;

import com.gepardec.core.services.UserService;
import com.gepardec.rest.api.UserResource;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import com.gepardec.rest.model.dto.UserRestDto;
import com.gepardec.rest.model.mapper.UserRestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@RequestScoped
public class UserResourceImpl implements UserResource {

  @Inject
  private UserService userService;
  @Inject
  private UserRestMapper mapper;

  @Override
  public Response createUser(CreateUserCommand userCommand) {
    return userService.saveUser(mapper.createUserCommandtoUser(userCommand)).map(UserRestDto::new)
        .map(userRestDto -> Response.status(Status.CREATED).entity(userRestDto))
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();

  }

  @Override
  public Response updateUser(String token, UpdateUserCommand updateUserCommand) {
    return userService.updateUser(mapper.updateUserCommandtoUser(token, updateUserCommand))
        .map(UserRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }


  @Override
  public Response getUser(String token) {
    return userService.findUserByToken(token).map(UserRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }


  @Override
  public Response getUsers(Boolean includeDeactivated) {
    return Response.ok(userService.findAllUsers(includeDeactivated)
            .stream()
            .map(UserRestDto::new)
            .toList())
        .build();

  }

  @Override
  public Response deleteUser(String token) {
    return userService.deleteUser(token).map(UserRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
  }

}
