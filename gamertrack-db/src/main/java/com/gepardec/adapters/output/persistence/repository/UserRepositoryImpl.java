package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.adapters.output.persistence.repository.mapper.Mapper;
import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.User;
import com.gepardec.model.dto.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, Serializable {

  private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  @PersistenceContext()
  protected EntityManager entityManager;
  @Inject
  Mapper mapper;

  @Override
  public Optional<User> saveUser(UserDto userDto) {
    User user = mapper.toUser(userDto);
    //User user = entityManager.getReference(User.class,userDto.id());
    entityManager.persist(user);
    User userSaved = entityManager.find(User.class, user.getId());
    log.info("Saved user with id: {}", userSaved.getId());
    return Optional.ofNullable(userSaved);
  }

  @Override
  public Optional<User> updateUser(UserDto userDto) {
    log.info("updating user with id: {}", userDto.id());

    User user = mapper.toExistingUser(userDto, entityManager.find(User.class, userDto.id()));
    entityManager.merge(user);
    User usermerged = entityManager.find(User.class, user.getId());
    log.info("Updated user with id: {}", usermerged.getId());
    return Optional.ofNullable(usermerged);
  }


  @Override
  public void deleteUser(UserDto userDto) {
    User user = mapper.toExistingUser(userDto,entityManager.find(User.class, userDto.id()));
    log.info("Deleted user with id: {}", userDto.id());
    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", userDto.id(),userDto.firstname(),userDto.lastname(),userDto.deactivated());
    log.info("deleting: user WITH NO SCORES with the id {} firstname {} lastname {} deactivated {} is present", user.getId(),user.getFirstname(),user.getLastname(),user.isDeactivated());
    entityManager.remove(user);
  }
  @Override
  public List<User> findAllUsersIncludeDeleted() {
    List<User> resultList = entityManager.createQuery("SELECT u FROM User u", User.class)
        .getResultList();
    log.info("Find all users including deleted user. Returned list of size:{}", resultList.size());

    return resultList;
  }

  @Override
  public List<User> findAllUsers() {
    List<User> resultList = entityManager.createQuery(
            "SELECT u FROM User u Where u.deactivated = false", User.class)
        .getResultList();
    log.info("Find all users. Returned list of size:{}", resultList.size());
    return resultList;

  }

  @Override
  public Optional<User> findUserById(long id) {
    List<User> resultList = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.id = :id AND u.deactivated = false",
            User.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user with id: {}. Returned list of size:{}", id, resultList.size());
    return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
  }

  @Override
  public Optional<User> findUserByIdIncludeDeleted(long id) {
    List<User> resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id",
            User.class)
        .setParameter("id", id)
        .getResultList();
    log.info("Find user including deleted with id: {}. Returned list of size:{}", id,
        resultList.size());
    return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
  }

  @Override
  public Optional<User> findUserReferencesById(Long userId) {
    return Optional.of(entityManager.getReference(User.class, userId));
  }

  @Override
  public Boolean existsByUserId(List<Long> userIds) {
    long foundUserIds = userIds.stream()
        .map(this::findUserById)
        .count();

    return foundUserIds == userIds.size();
  }
}
