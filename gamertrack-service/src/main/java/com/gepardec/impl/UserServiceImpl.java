package com.gepardec.impl;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
@Stateless
public class UserServiceImpl implements UserService, Serializable {

    @Inject
    private UserRepository userRepository;

    @Override
    public Optional<User> saveUser(User user) {
        return userRepository.saveUser(user);
    }


    @Override
    public Optional<User> updateUser(Long id, User userEdit) {
        Optional<User> entity = userRepository.findUserById(id);
        if(entity.isPresent()) {
            User user = entity.get();
            user.setFirstname(userEdit.getFirstname());
            user.setLastname(userEdit.getLastname());
            return userRepository.saveUser(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUser(Long id) {
        User user=null;
        Optional<User> entity = userRepository.findUserById(id);

            if(entity.isPresent()){
                user=entity.get();
                userRepository.deleteUser(user);
                return Optional.of(user);
            }
            return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public Optional<User> findUserById(long id) {
        Optional<User> entity = userRepository.findUserById(id);
        if(entity.isPresent()){
            return userRepository.findUserById(id);
        }
        return Optional.empty();
    }
}
