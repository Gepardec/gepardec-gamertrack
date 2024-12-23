package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.EntityMapper;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class UserRepositoryImpl implements UserRepository, Serializable {

  private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  @Inject
  protected EntityManager entityManager;
  @Inject
  EntityMapper entityMapper;

  @Override
  public Optional<UserEntity> saveUser(User userDto) {
    UserEntity user = entityMapper.toUser(userDto);
    //User user = entityManager.getReference(User.class,userDto.id());
    entityManager.persist(user);
    UserEntity userSaved = entityManager.find(UserEntity.class, user.getId());
    log.info("Saved user with id: {}", userSaved.getId());
    return Optional.ofNullable(userSaved);
  }

  @Override
  public Optional<UserEntity> updateUser(User userDto) {
    log.info("updating user with id: {}", userDto.id());

    UserEntity user = entityMapper.toExistingUser(userDto, entityManager.find(UserEntity.class, userDto.id()));
    entityManager.merge(user);
    UserEntity usermerged = entityManager.find(UserEntity.class, user.getId());
    log.info("Updated user with id: {}", usermerged.getId());
    return Optional.ofNullable(usermerged);
  }


  @Override
  public void deleteUser(User userDto) {
    UserEntity user = entityMapper.toExistingUser(userDto,entityManager.find(UserEntity.class, userDto.id()));
    log.info("Deleted user with id: {}", userDto.id());
    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", userDto.id(),userDto.firstname(),userDto.lastname(),userDto.deactivated());
    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", user.getId(),user.getFirstname(),user.getLastname(),user.isDeactivated());
    entityManager.remove(user);
  }

  @Override
  public void deleteAllUsers() {
    entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
    log.info("Deleted all users. size: {}", findAllUsers().size());
  }

  @Override
  public List<UserEntity> findAllUsersIncludeDeleted() {
    List<UserEntity> resultList = entityManager.createQuery("SELECT u FROM UserEntity u", UserEntity.class)
        .getResultList();
    log.info("Find all users including deleted user. Returned list of size:{}", resultList.size());

    return resultList;
  }

  @Override
  public List<UserEntity> findAllUsers() {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u Where u.deactivated = false", UserEntity.class)
        .getResultList();
    log.info("Find all users. Returned list of size:{}", resultList.size());
    return resultList;

  }

  @Override
  public Optional<UserEntity> findUserById(long id) {
    List<UserEntity> resultList = entityManager.createQuery(
            "SELECT u FROM UserEntity u WHERE u.id = :id AND u.deactivated = false",
            UserEntity.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user with id: {}. Returned list of size:{}", id, resultList.size());
    return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
  }

  @Override
  public Optional<UserEntity> findUserByIdIncludeDeleted(long id) {
    List<UserEntity> resultList = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.id = :id",
            UserEntity.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user including deleted with id: {}. Returned list of size:{}", id,
        resultList.size());
    return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
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
