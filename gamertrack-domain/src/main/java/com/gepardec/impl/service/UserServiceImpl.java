package com.gepardec.impl.service;

import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.GameService;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.TokenService;
import com.gepardec.core.services.UserService;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Transactional
@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Inject
    UserRepository userRepository;

    @Inject
    ScoreService scoreService;
    @Inject
    TokenService tokenService;
    @Inject
    GameService gameService;

    @Override
    public Optional<User> saveUser(User user) {
        String token = tokenService.generateToken();
        user.setToken(token);
        Optional<User> savedUser = userRepository.saveUser(user);
        for (Game game : gameService.findAllGames()) {
            scoreService.saveScore(new Score(0L,savedUser.get(),game,1500L,"",true));
        }
        return savedUser;
    }


    @Override
    public Optional<User> updateUser(User user) {
        Optional<User> entity = userRepository.findUserByToken(user.getToken());
        if(entity.isPresent()) {
            log.info("updating: user with the token {} is present", user.getId());
            return userRepository.updateUser(user);
        }
        log.error("Could not find user with token {}. user was not updated", user.getToken());
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUser(String token) {
        Optional<User> user = userRepository.findUserByToken(token);

            if(user.isPresent()){
                log.info("deleting: user with the token {} is present", token);
                List<Score> scoresByUser = scoreService.findScoresByUser(user.get().getToken(),true);
                if(scoresByUser.stream().allMatch(Score::isDeletable)) {
                    log.info("user with the token {} has just default scores stored.", token);

                    for(Score score : scoresByUser) {
                        scoreService.deleteScore(score.getToken());
                    }
                    log.info("deleting: user WITH DEFAULT SCORES with the token {} firstname {} lastname {} deactivated {} is present", user.get().getToken(),user.get().getFirstname(),user.get().getLastname(),user.get().isDeactivated());
                    userRepository.deleteUser(user.get());
                }
                else {
                    user.get().setDeactivated(true);
                    log.info("user with the token {} was deactivated", token);
                    userRepository.updateUser(user.get());
                }
                return user;
            }
        log.error("Could not find user with token {}. User was not deleted", token);
        return Optional.empty();
    }

    @Override
    public List<User> findAllUsers(boolean includeDeactivated) {
        return userRepository.findAllUsersSortedByMatchCount(includeDeactivated);
    }

    @Override
    public Optional<User> findUserByToken(String token) {
            return userRepository.findUserByToken(token);
    }
}
