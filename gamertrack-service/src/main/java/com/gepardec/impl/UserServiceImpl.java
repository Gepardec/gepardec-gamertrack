package com.gepardec.impl;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.ScoreService;
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

    @Inject
    private ScoreService scoreService;

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
                if(scoreService.findScoreByUser(entity.get().getId()).isEmpty()){
                    user=entity.get();
                    userRepository.deleteUser(user);
                }
                else {
                    user=entity.get();
                    user.setFirstname("DELETED");
                    user.setLastname("U$ER");
                    userRepository.updateUser(entity.get().getId(),user);
                }
                return Optional.of(user);
            }
            return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public List<User> findAllUsersIncludeDeleted() {
        return userRepository.findAllUsersIncludeDeleted();
    }

    @Override
    public Optional<User> findUserById(long id) {
            return userRepository.findUserById(id);
    }
}
