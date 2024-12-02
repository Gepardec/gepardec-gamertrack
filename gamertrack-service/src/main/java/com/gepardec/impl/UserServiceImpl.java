package com.gepardec.impl;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.ScoreService;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateful;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Stateless
public class UserServiceImpl implements UserService, Serializable {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
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
            log.info("updating: user with the id {} is present", id);
            User user = entity.get();
            user.setFirstname(userEdit.getFirstname());
            user.setLastname(userEdit.getLastname());
            return userRepository.saveUser(user);
        }
        log.error("Could not find user with id {}. user was not updated", id);
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUser(Long id) {
        User user=null;
        Optional<User> entity = userRepository.findUserById(id);

            if(entity.isPresent()){
                log.info("deleting: user with the id {} is present", id);
                List<Score> scoresByUser = scoreService.findScoreByUser(entity.get().getId());
                if(scoresByUser.isEmpty()){
                    user=entity.get();
                    log.info("user with the id {} has no scores stored. deleting user", id);
                    userRepository.deleteUser(user);
                }
                else {
                    user=entity.get();
                    user.setFirstname("DELETED");
                    user.setLastname("U$ER");
                    log.info("user with the id {} has {} scores stored. user was deactivated", id, scoresByUser.size());
                    userRepository.saveUser(user);
                }
                return Optional.of(user);
            }
        log.error("Could not find user with id {}. User was not deleted", id);
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

    @Override
    public Optional<User> findUserByIdIncludeDeleted(long id) {
        return userRepository.findUserByIdIncludeDeleted(id);
    }
}
