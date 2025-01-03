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
import java.util.stream.Collectors;

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
    assertTrue(userRepository.findUserByToken(user.getToken()).isPresent());
  }

  @Test
  void ensureUpdateUserWorks() {
    User user = new User(1L, "OLD", "USER", false,tokenService.generateToken());
    userRepository.saveUser(user);

    User updatedUser = new User(1L, "NEW", "USER", false,user.getToken());

    userRepository.updateUser(updatedUser);

    Optional<User> foundUser = userRepository.findUserByToken(user.getToken());

    assertTrue(foundUser.isPresent());
    assertEquals(foundUser.get().getFirstname(), updatedUser.getFirstname());
  }

  @Test
  void ensureDeleteUserWorks() {
    User user = TestFixtures.user(1L);
    userRepository.saveUser(user);

    int sizeBefore = userRepository.findAllUsers(true).size();

    userRepository.deleteUser(user);

    assertEquals(1, sizeBefore);
    assertEquals(0, userRepository.findAllUsers(true).size());
    assertFalse(userRepository.findUserByToken(user.getToken()).isPresent());
  }

  @Test
  void ensureDeleteAllUsersWorks() {
    List<User> users = TestFixtures.users(4);
    userRepository.saveUser(users.get(0));
    userRepository.saveUser(users.get(1));
    userRepository.saveUser(users.get(2));
    userRepository.saveUser(users.get(3));

    int sizeBefore = userRepository.findAllUsers(true).size();

    userRepository.deleteAllUsers();

    assertEquals(4, sizeBefore);
    assertEquals(0, userRepository.findAllUsers(true).size());
    assertFalse(userRepository.findUserByToken(users.get(1).getToken()).isPresent());
  }

  @Test
  void ensureFindAllUsers() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = new User(3L, "Test", "Arbeit", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(2, userRepository.findAllUsers(false).size());
  }

  @Test
  void ensureFindAllUsersIncludeDeleted() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = new User(3L, "Test", "deleted", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsers(true).size());
  }

  @Test
  void ensureFindUserByTokenWorks() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = TestFixtures.user(3L);

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsers(true).size());
    assertTrue(userRepository.findUserByToken(user1.getToken()).isPresent());
  }

  @Test
  void ensureFindUserByTokenWorksIncludedDeleted() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = TestFixtures.user(3L);
    user3.setDeactivated(true);

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertTrue(userRepository.findUserByToken(user3.getToken()).isPresent());
  }

  @Test
  void ensureExistsByUserTokenWorks() {

    List<User> users = TestFixtures.users(4);
    userRepository.saveUser(users.get(0));
    userRepository.saveUser(users.get(1));
    userRepository.saveUser(users.get(2));
    userRepository.saveUser(users.get(3));
    userRepository.saveUser(users.get(4));

    assertTrue(userRepository.existsByUserToken(users.stream().map(User::getToken).collect(Collectors.toList())));
    assertFalse(userRepository.existsByUserToken(List.of("po2k3s-do32köaw", "lo2egsa-9dm3mdj")));

  }

}
