package com.gepardec.interfaces.services;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> saveUser(User user);
    Optional<User> updateUser(Long id, User user);
    Optional<User> deleteUser(Long id);
    List<User> findAllUsers();
    List<User> findAllUsersIncludeDeleted();
    Optional<User> findUserById(long id);
    Optional<User> findUserByIdIncludeDeleted(long id);
}
