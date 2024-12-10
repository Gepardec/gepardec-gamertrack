package com.gepardec.interfaces.services;

import com.gepardec.model.User;
import com.gepardec.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> saveUser(UserDto userDto);
    Optional<User> updateUser(UserDto userDto);
    Optional<User> deleteUser(Long id);
    List<User> findAllUsers();
    List<User> findAllUsersIncludeDeleted();
    Optional<User> findUserById(long id);
    Optional<User> findUserByIdIncludeDeleted(long id);
}
