package com.gepardec.service;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import jakarta.data.repository.Query;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public Optional<User> saveUser(User user) {
        if(userRepository.findUserById(user.getId()).isEmpty()) {
            return userRepository.saveUser(user);
        }
        //throw Exception
        return Optional.empty();
    }


    @Override
    public Optional<User> updateUser(User user) {
        if(userRepository.findUserById(user.getId()).isPresent()) {
            return userRepository.updateUser(user);
        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(User user) {
        if(userRepository.findUserById(user.getId()).isPresent()) {
             userRepository.deleteUser(user);
        }
    }

    @Override
    public Optional<List<User>> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public Optional<User> findUserById(long id) {
        return userRepository.findUserById(id);
    }
}
