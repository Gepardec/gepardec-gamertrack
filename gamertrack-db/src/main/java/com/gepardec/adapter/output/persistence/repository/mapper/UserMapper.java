package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper {

  public UserEntity userModelToUserEntity(User user) {
    return new UserEntity(user.getFirstname(), user.getLastname(), user.isDeactivated(),user.getToken());
  }

  public User userEntityToUserModel(UserEntity userEntity) {
    return new User(userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(),
        userEntity.isDeactivated(), userEntity.getToken());
  }

  public UserEntity userModeltoExistingUserEntity(User user, UserEntity userEntity) {
    userEntity.setFirstname(user.getFirstname());
    userEntity.setLastname(user.getLastname());
    userEntity.setDeactivated(user.isDeactivated());
    return userEntity;
  }
}
