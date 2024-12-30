package com.gepardec.rest.model.mapper;

import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRestMapper {

  public User createUserCommandtoUser(CreateUserCommand createUserCommand) {
    return new User(null, createUserCommand.firstname(), createUserCommand.lastname(), false,null);
  }

  public User updateUserCommandtoUser(Long id, UpdateUserCommand updateUserCommand) {
    return new User(id, updateUserCommand.firstname(), updateUserCommand.lastname(),
        updateUserCommand.deactivated(),null);
  }
}
