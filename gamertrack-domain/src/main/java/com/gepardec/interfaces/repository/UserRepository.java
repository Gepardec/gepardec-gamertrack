package com.gepardec.interfaces.repository;

import com.gepardec.model.User;

import java.util.Optional;

public interface UserRepository {
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    Optional<User> getUsers();
    User getUserById(long id);
}
