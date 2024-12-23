package com.gepardec.core.services;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> saveUser(User userDto);
    Optional<User> updateUser(User userDto);
    Optional<User> deleteUser(Long id);
    List<User> findAllUsers();
    List<User> findAllUsersIncludeDeleted();
    Optional<User> findUserById(long id);
    Optional<User> findUserByIdIncludeDeleted(long id);
}
