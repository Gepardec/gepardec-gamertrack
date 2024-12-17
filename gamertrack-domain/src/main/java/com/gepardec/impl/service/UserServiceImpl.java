package com.gepardec.impl.service;

import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.UserService;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.UserDto;
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

    @Override
    public Optional<User> saveUser(UserDto userDto) {
        return userRepository.saveUser(userDto);
    }


    @Override
    public Optional<User> updateUser(UserDto userDto) {
        Optional<User> entity = userRepository.findUserById(userDto.id());
        if(entity.isPresent()) {
            log.info("updating: user with the id {} is present", userDto.id());
            if(!entity.get().isDeactivated()) {
                return userRepository.updateUser(userDto);
            }
            log.error("User with id {}. is deactivated and was not updated", userDto.id());
            return Optional.empty();
        }
        log.error("Could not find user with id {}. user was not updated", userDto.id());
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUser(Long id) {
        Optional<User> user = userRepository.findUserById(id);

            if(user.isPresent()){
                log.info("deleting: user with the id {} is present", id);
                List<Score> scoresByUser = scoreService.findScoreByUser(user.get().getId());
                if(scoresByUser.isEmpty()){
                    log.info("user with the id {} has no scores stored. deleting user", id);

                    UserDto userDto = new UserDto(user.get());
                    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", userDto.id(),userDto.firstname(),userDto.lastname(),userDto.deactivated());
                    userRepository.deleteUser(userDto);
                }
                else {
                    user.get().setDeactivated(true);
                    UserDto userDto = new UserDto(user.get());
                    log.info("deleting: user WITH SCORES with the id {} firstname {} lastname {} deactivated {} is present", userDto.id(),userDto.firstname(),userDto.lastname(),userDto.deactivated());

                    log.info("user with the id {} has {} scores stored. user was deactivated", id, scoresByUser.size());
                    userRepository.updateUser(userDto);
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
