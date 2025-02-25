package com.gepardec.core.repository;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {
  Optional<User> saveUser(User user);
  Optional<User> updateUser(User user);
  void deleteUser(User user);
  List<User> findAllUsersSortedByMatchCount(boolean includeDeactivatedUsers);
  Optional<User> findUserByToken(String token);
  Boolean existsByUserToken(List<String> userTokens);
}
