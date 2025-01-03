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
  List<User> findAllUsers(boolean includeDeactivatedUsers);
  Optional<User> findUserByToken(String token);
  Boolean existsByUserToken(List<String> userTokens);

}
