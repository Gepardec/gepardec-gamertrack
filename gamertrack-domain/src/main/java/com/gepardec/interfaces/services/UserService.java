package com.gepardec.interfaces.services;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> saveUser(User user);
    Optional<User> updateUser(User user);
    void deleteUser(User user);
    Optional<List<User>> findAllUsers();
    Optional<User> findUserById(long id);
}
