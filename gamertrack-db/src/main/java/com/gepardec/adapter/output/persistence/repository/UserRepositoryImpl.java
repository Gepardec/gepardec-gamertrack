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
        entityManager.find(UserEntity.class, user.getId()));
    entityManager.merge(userEntity);
    UserEntity usermerged = entityManager.find(UserEntity.class, user.getId());
    log.info("Updated user with id: {}", usermerged.getId());
    return Optional.ofNullable(userMapper.userEntityToUserModel(usermerged));
  }


  @Override
  public void deleteUser(User user) {
    UserEntity userEntity = userMapper.userModeltoExistingUserEntity(user,
        entityManager.find(UserEntity.class, user.getId()));
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
    log.info("Deleted all users. size: {}", findAllUsers().size());
  }

  @Override
  public List<User> findAllUsersIncludeDeleted() {
    List<UserEntity> resultList = entityManager.createQuery("SELECT u FROM UserEntity u",
            UserEntity.class)
        .getResultList();
    log.info("Find all users including deleted user. Returned list of size:{}", resultList.size());

    return resultList.stream().map(userMapper::userEntityToUserModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findAllUsers() {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u Where u.deactivated = false", UserEntity.class)
        .getResultList();
    log.info("Find all users. Returned list of size:{}", resultList.size());
    return resultList.stream().map(userMapper::userEntityToUserModel)
        .collect(Collectors.toList());

  }

  @Override
  public Optional<User> findUserById(long id) {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.id = :id AND u.deactivated = false",
            UserEntity.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user with id: {}. Returned list of size:{}", id, resultList.size());
    return resultList.isEmpty()
        ? Optional.empty()
        : Optional.of(userMapper.userEntityToUserModel(resultList.getFirst()));
  }

  @Override
  public Optional<User> findUserByIdIncludeDeleted(long id) {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.id = :id",
            UserEntity.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user including deleted with id: {}. Returned list of size:{}", id,
        resultList.size());
    return resultList.isEmpty()
        ? Optional.empty()
        : Optional.of(userMapper.userEntityToUserModel(resultList.getFirst()));
  }

  @Override
  public Boolean existsByUserId(List<Long> userIds) {
    long foundUserIds = userIds.stream()
        .map(this::findUserById)
        .filter(Optional::isPresent)
        .count();
    return foundUserIds == userIds.size();
  }
}
