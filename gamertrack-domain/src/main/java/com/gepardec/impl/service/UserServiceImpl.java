package com.gepardec.impl.service;

import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.TokenService;
import com.gepardec.core.services.UserService;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
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
    @Inject
    private TokenService tokenService;

    @Override
    public Optional<User> saveUser(User user) {
        String token = tokenService.generateToken();
        System.out.println("Token: " + token);
        user.setToken(token);
        return userRepository.saveUser(user);
    }


    @Override
    public Optional<User> updateUser(User user) {
        Optional<User> entity = userRepository.findUserById(user.getId());
        if(entity.isPresent()) {
            log.info("updating: user with the id {} is present", user.getId());
            if(!entity.get().isDeactivated()) {
                return userRepository.updateUser(user);
            }
            log.error("User with id {}. is deactivated and was not updated", user.getId());
            return Optional.empty();
        }
        log.error("Could not find user with id {}. user was not updated", user.getId());
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUser(Long id) {
        Optional<User> user = userRepository.findUserById(id);

            if(user.isPresent()){
                log.info("deleting: user with the id {} is present", id);
                List<Score> scoresByUser = scoreService.findScoresByUser(user.get().getToken(),true);
                if(scoresByUser.isEmpty()){
                    log.info("user with the id {} has no scores stored. deleting user", id);

                    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", user.get().getId(),user.get().getFirstname(),user.get().getLastname(),user.get().isDeactivated());
                    userRepository.deleteUser(user.get());
                }
                else {
                    user.get().setDeactivated(true);
                    log.info("deleting: user WITH SCORES with the id {} firstname {} lastname {} deactivated {} is present", user.get().getId(),user.get().getFirstname(),user.get().getLastname(),user.get().isDeactivated());

                    log.info("user with the id {} has {} scores stored. user was deactivated", id, scoresByUser.size());
                    userRepository.updateUser(user.get());
                }
                return user;
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
