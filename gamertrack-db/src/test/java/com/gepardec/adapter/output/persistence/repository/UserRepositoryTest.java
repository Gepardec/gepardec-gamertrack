package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@io.quarkus.test.junit.QuarkusTest
public class UserRepositoryTest  {

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
  MatchRepository matchRepository;

  @Inject
  GameRepository gameRepository;

  @Inject
  TokenService tokenService;

  @BeforeEach
  public void before() throws Exception {
    
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

    int sizeBefore = userRepository.findAllUsersSortedByMatchCount(true).size();

    userRepository.deleteUser(user);

    assertEquals(1, sizeBefore);
    assertEquals(0, userRepository.findAllUsersSortedByMatchCount(true).size());
    assertFalse(userRepository.findUserByToken(user.getToken()).isPresent());
  }

  @Test
  void ensureFindAllUsers() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = new User(3L, "Test", "Arbeit", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(2, userRepository.findAllUsersSortedByMatchCount(false).size());
  }

  @Test
  void ensureFindAllUsersIncludeDeleted() {

    User user1 = TestFixtures.user(10L);
    User user2 = TestFixtures.user(20L);
    User user3 = new User(30L, "Test", "deleted", true,tokenService.generateToken());

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsersSortedByMatchCount(true).size());
  }

  @Test
  void ensureFindUserByTokenWorks() {

    User user1 = TestFixtures.user(1L);
    User user2 = TestFixtures.user(2L);
    User user3 = TestFixtures.user(3L);

    userRepository.saveUser(user1);
    userRepository.saveUser(user2);
    userRepository.saveUser(user3);

    assertEquals(3, userRepository.findAllUsersSortedByMatchCount(true).size());
    assertTrue(userRepository.findUserByToken(user1.getToken()).isPresent());
  }

  @Test
  void ensureFindUserByTokenWorksIncludedDeleted() {

    User user1 = TestFixtures.user(10L);
    User user2 = TestFixtures.user(20L);
    User user3 = TestFixtures.user(30L);
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

  @Test
  void ensureFindAllUsersSortedByMatchCountWorks() {
    List<Match> matches = TestFixtures.matches(5);
    Game game = gameRepository.saveGame(TestFixtures.games(1).getFirst()).get();
    List<User> users = TestFixtures.users(4);
    User user1 = userRepository.saveUser(users.getFirst()).get();
    User user2 = userRepository.saveUser(users.getLast()).get();
    User user3 = userRepository.saveUser(users.get(2)).get();
    List<Match> matches2 = TestFixtures.matches(10);
    matches.forEach(match -> { match.setUsers(List.of(user1)); match.setGame(game);});
    matches2.forEach(match -> { match.setUsers(List.of(user2)); match.setGame(game);});
    matches.forEach(matchRepository::saveMatch);
    matches2.forEach(matchRepository::saveMatch);

    List<Match> matchList = matchRepository.findAllMatches();
    var foundUsers = userRepository.findAllUsersSortedByMatchCount(false);

    matchList.forEach(System.out::println);
    foundUsers.forEach(System.out::println);
  }
}
