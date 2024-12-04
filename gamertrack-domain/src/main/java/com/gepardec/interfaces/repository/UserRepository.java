package com.gepardec.interfaces.repository;

import com.gepardec.model.User;
import java.util.List;
import java.util.Optional;


public interface UserRepository {

  List<User> findAllUsers();

  List<User> findAllUsersIncludeDeleted();

  Optional<User> findUserById(long id);

  Optional<User> findUserByIdIncludeDeleted(long id);

  Optional<User> saveUser(User user);

  void deleteUser(User user);

  Optional<User> findUserReferencesById(Long userId);
}
