package com.gepardec.core.services;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> saveUser(User user);
    Optional<User> updateUser(User user);
    Optional<User> deleteUser(String token);
    List<User> findAllUsers(boolean includeDeactivated);
    Optional<User> findUserByToken(String Token);
}
