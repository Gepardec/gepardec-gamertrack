package com.gepardec.core.repository;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {


  Optional<User> saveUser(User user);
  Optional<User> updateUser(User user);

  void deleteUser(User user);
  /**
   * deleteAllUsers - Only for testing purposes
   */
  void deleteAllUsers();

  List<User> findAllUsers();

  List<User> findAllUsersIncludeDeleted();

  Optional<User> findUserById(long id);

  Optional<User> findUserByIdIncludeDeleted(long id);

  Boolean existsByUserId(List<Long> userIds);
}
