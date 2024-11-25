package com.gepardec.interfaces.services;

import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    Optional<User> getUsers();
    User getUserById(long id);
}
