package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
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
  Mapper mapper;

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.User> saveUser(User userDto) {
    com.gepardec.adapter.output.persistence.entity.User user = mapper.toUser(userDto);
    //User user = entityManager.getReference(User.class,userDto.id());
    entityManager.persist(user);
    com.gepardec.adapter.output.persistence.entity.User userSaved = entityManager.find(com.gepardec.adapter.output.persistence.entity.User.class, user.getId());
    log.info("Saved user with id: {}", userSaved.getId());
    return Optional.ofNullable(userSaved);
  }

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.User> updateUser(User userDto) {
    log.info("updating user with id: {}", userDto.id());

    com.gepardec.adapter.output.persistence.entity.User user = mapper.toExistingUser(userDto, entityManager.find(com.gepardec.adapter.output.persistence.entity.User.class, userDto.id()));
    entityManager.merge(user);
    com.gepardec.adapter.output.persistence.entity.User usermerged = entityManager.find(com.gepardec.adapter.output.persistence.entity.User.class, user.getId());
    log.info("Updated user with id: {}", usermerged.getId());
    return Optional.ofNullable(usermerged);
  }


  @Override
  public void deleteUser(User userDto) {
    com.gepardec.adapter.output.persistence.entity.User user = mapper.toExistingUser(userDto,entityManager.find(com.gepardec.adapter.output.persistence.entity.User.class, userDto.id()));
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
  public List<com.gepardec.adapter.output.persistence.entity.User> findAllUsersIncludeDeleted() {
    List<com.gepardec.adapter.output.persistence.entity.User> resultList = entityManager.createQuery("SELECT u FROM User u", com.gepardec.adapter.output.persistence.entity.User.class)
        .getResultList();
    log.info("Find all users including deleted user. Returned list of size:{}", resultList.size());

    return resultList;
  }

  @Override
  public List<com.gepardec.adapter.output.persistence.entity.User> findAllUsers() {
    List<com.gepardec.adapter.output.persistence.entity.User> resultList = entityManager.createQuery(
            "SELECT u FROM User u Where u.deactivated = false", com.gepardec.adapter.output.persistence.entity.User.class)
        .getResultList();
    log.info("Find all users. Returned list of size:{}", resultList.size());
    return resultList;

  }

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.User> findUserById(long id) {
    List<com.gepardec.adapter.output.persistence.entity.User> resultList = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.id = :id AND u.deactivated = false",
            com.gepardec.adapter.output.persistence.entity.User.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user with id: {}. Returned list of size:{}", id, resultList.size());
    return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
  }

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.User> findUserByIdIncludeDeleted(long id) {
    List<com.gepardec.adapter.output.persistence.entity.User> resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id",
            com.gepardec.adapter.output.persistence.entity.User.class)
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
