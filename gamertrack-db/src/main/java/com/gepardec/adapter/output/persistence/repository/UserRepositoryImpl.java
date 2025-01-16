package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.UserMapper;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class UserRepositoryImpl implements UserRepository, Serializable {

  private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  @Inject
  protected EntityManager entityManager;
  @Inject
  UserMapper userMapper;

  @Override
  public Optional<User> saveUser(User user) {
    UserEntity userEntity = userMapper.userModelToUserEntity(user);
    entityManager.persist(userEntity);
    UserEntity userSaved = entityManager.find(UserEntity.class, userEntity.getId());
    log.info("Saved user with id: {}", userEntity.getId());
    return Optional.ofNullable(userMapper.userEntityToUserModel(userSaved));
  }

  @Override
  public Optional<User> updateUser(User user) {
    log.info("updating user with id: {}", user.getId());

    UserEntity userEntity = userMapper.userModeltoExistingUserEntity(user,
        entityManager.find(UserEntity.class, findUserByToken(user.getToken()).get().getId()));
    entityManager.merge(userEntity);
    UserEntity usermerged = entityManager.find(UserEntity.class, userEntity.getId());
    log.info("Updated user with id: {}", usermerged.getId());
    return Optional.ofNullable(userMapper.userEntityToUserModel(usermerged));
  }


  @Override
  public void deleteUser(User user) {
    UserEntity userEntity = userMapper.userModeltoExistingUserEntity(user,
        entityManager.find(UserEntity.class, findUserByToken(user.getToken()).get().getId()));
    log.info("Deleted user with id: {}", user.getId());
    log.info(
        "deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present",
        userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(),
        userEntity.isDeactivated());
    entityManager.remove(userEntity);
  }

  @Override
  public void deleteAllUsers() {
    entityManager.createQuery("DELETE FROM UserEntity ").executeUpdate();
    log.info("Deleted all users. size: {}", findAllUsers(false).size());
  }

  @Override
  public List<User> findAllUsers(boolean includeDeactivatedUsers) {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u " +
                    "Where(:includeDeactivatedUsers = true OR u.deactivated = false) "
                    , UserEntity.class)
            .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
            .getResultList();
    log.info("Find all users. Returned list of size:{}", resultList.size());
    return resultList.stream().map(userMapper::userEntityToUserModel)
        .collect(Collectors.toList());

  }

  @Override
  public Optional<User> findUserByToken(String token) {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u " +
                    "WHERE u.token = :token ", UserEntity.class)
            .setParameter("token", token)
            .getResultList();
    log.info("Find user with token: {}. Returned list of size:{}", token, resultList.size());
    return resultList.isEmpty()
        ? Optional.empty()
        : Optional.of(userMapper.userEntityToUserModel(resultList.getFirst()));
  }

  @Override
  public Boolean existsByUserToken(List<String> userTokens) {
    long foundUserTokens = userTokens.stream()
        .map(this::findUserByToken)
        .filter(Optional::isPresent)
        .count();
    return foundUserTokens == userTokens.size();
  }
}
