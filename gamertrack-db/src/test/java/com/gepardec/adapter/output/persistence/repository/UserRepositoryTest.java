package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ArquillianExtension.class)
public class UserRepositoryTest extends GamertrackDbIT {

  /*

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-----Using SavedId only temporary. Soon will be replaced with uuid/Base58-------
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------

   */
  @PersistenceContext
  EntityManager entityManager;

  @Inject
  UserRepository userRepository;

  @Inject
  TokenService tokenService;

  @BeforeEach
  public void before() {
    userRepository.deleteAllUsers();
    entityManager.clear();
  }

  @Test
  void ensureSaveUserWorks() {
    User user = TestFixtures.user(1L);
    Long savedId = userRepository.saveUser(user).get().getId();
    assertTrue(userRepository.findUserById(savedId).isPresent());
  }

  @Test
  void ensureUpdateUserWorks() {
    User user = new User(1L, "OLD", "USER", false,tokenService.generateToken());
    Long savedId = userRepository.saveUser(user).get().getId();

    User updatedUser = new User(savedId, "NEW", "USER", false,tokenService.generateToken());

    userRepository.updateUser(updatedUser);

    Optional<User> foundUser = userRepository.findUserById(savedId);

    assertTrue(foundUser.isPresent());
    assertEquals(foundUser.get().getFirstname(), updatedUser.getFirstname());
  }

  @Test
  void ensureDeleteUserWorks() {
    User user = TestFixtures.user(1L);
    userRepository.saveUser(user);

    int sizeBefore = userRepository.findAllUsers().size();

    userRepository.deleteUser(user);

    assertEquals(1, sizeBefore);
    assertEquals(0, userRepository.findAllUsers().size());
    assertFalse(userRepository.findUserById(1L).isPresent());
  }

  @Test
  void ensureDeleteAllUsersWorks() {
    List<User> users = TestFixtures.users(4);
    userRepository.saveUser(users.get(0));
    userRepository.saveUser(users.get(1));
    userRepository.saveUser(users.get(2));
    userRepository.saveUser(users.get(3));

    int sizeBefore = userRepository.findAllUsers().size();

    userRepository.deleteAllUsers();

    assertEquals(4, sizeBefore);
    assertEquals(0, userRepository.findAllUsers().size());
    assertFalse(userRepository.findUserById(1L).isPresent());
  }

  @Test
  void ensureFindAllUsers() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = new User(3L, "Test", "Arbeit", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(2, userRepository.findAllUsers().size());
  }

  @Test
  void ensureFindAllUsersIncludeDeleted() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = new User(3L, "Test", "deleted", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsersIncludeDeleted().size());
  }

  @Test
  void ensureFindUserByIdWorks() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = TestFixtures.user(3L);

    Long savedId1 = userRepository.saveUser(user1).get().getId();
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsers().size());
    assertTrue(userRepository.findUserById(savedId1).isPresent());
  }

  @Test
  void ensureFindUserByIdWorksIncludedDeleted() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = TestFixtures.user(3L);
    user3.setDeactivated(true);

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    Long savedId3 = userRepository.saveUser(user3).get().getId();

    assertTrue(userRepository.findUserByIdIncludeDeleted(savedId3).isPresent());
    assertTrue(userRepository.findUserById(savedId3).isEmpty());
  }

  @Test
  void ensureExistsByUserIdWorks() {

    List<User> users = TestFixtures.users(4);
    Long savedId1 = userRepository.saveUser(users.get(0)).get().getId();
    Long savedId2 = userRepository.saveUser(users.get(1)).get().getId();
    Long savedId3 = userRepository.saveUser(users.get(2)).get().getId();
    Long savedId4 = userRepository.saveUser(users.get(3)).get().getId();

    assertTrue(userRepository.existsByUserId(List.of(savedId1, savedId2, savedId3, savedId4)));
    assertFalse(userRepository.existsByUserId(List.of(1000L, 1001L)));

  }

}
