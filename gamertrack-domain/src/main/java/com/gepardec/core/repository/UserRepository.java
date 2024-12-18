package com.gepardec.core.repository;

import com.gepardec.model.User;
import com.gepardec.model.dto.UserDto;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;


public interface UserRepository {


  Optional<User> saveUser(UserDto userDto);
  Optional<User> updateUser(UserDto userDto);

  void deleteUser(UserDto userDto);

  List<User> findAllUsers();

  List<User> findAllUsersIncludeDeleted();

  Optional<User> findUserById(long id);

  Optional<User> findUserByIdIncludeDeleted(long id);

  Optional<User> findUserReferencesById(Long userId);

  Boolean existsByUserId(List<Long> userIds);
}
