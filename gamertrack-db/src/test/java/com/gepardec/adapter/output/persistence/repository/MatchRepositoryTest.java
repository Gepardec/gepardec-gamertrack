package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class MatchRepositoryTest extends GamertrackDbIT {

  @Inject
  private MatchRepository matchRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GameRepository gameRepository;


  @BeforeEach
  public void before() throws Exception {
    removeTableData(MatchEntity.class, GameEntity.class, UserEntity.class);
  }

  @Test
  public void ensureSaveAndReadMatchWorks() {
    Game game = TestFixtures.game(null);
    User user = TestFixtures.user(null);
    Optional<Game> savedGame = gameRepository.saveGame(game);
    System.out.println("gameid: " + savedGame.get().getId());
    Optional<User> savedUser = userRepository.saveUser(user);
    System.out.println("userid: " + savedUser.get().getId());
    Match match = new Match();
    match.setGame(savedGame.get());
    match.setUsers(List.of(savedUser.get()));

    var savedAndReadMatch = matchRepository.saveMatch(match);

    Assertions.assertTrue(savedAndReadMatch.isPresent());
    Assertions.assertEquals(match.getGame().getName(), savedAndReadMatch.get().getGame().getName());
    Assertions.assertEquals(match.getUsers().getFirst().getFirstname(),
        savedAndReadMatch.get().getUsers().getFirst().getFirstname());
  }

  @Test
  public void ensureSavingMatchWithInvalidReferencesFails() {
    Match match = new Match(null, TestFixtures.game(2L), List.of(TestFixtures.user(2L)));

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> matchRepository.saveMatch(match));
  }

  @Test
  public void ensureFindAllMatchesReturnsForExistingMatchesAllMatches() {
    Optional<Game> savedGame1 = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<Game> savedGame2 = gameRepository.saveGame(new Game(null, "TestName2", "No"));

    Optional<User> savedUser1 = userRepository.saveUser(
        new User(null, "Tesname", "LastName", false));
    Optional<User> savedUser2 = userRepository.saveUser(
        new User(null, "Tesname", "No", false));

    Match match1 = new Match(null, savedGame1.get(), List.of(savedUser1.get()));
    Match match2 = new Match(null, savedGame2.get(), List.of(savedUser2.get()));

    Optional<Match> savedMatch1 = matchRepository.saveMatch(match1);
    Optional<Match> savedMatch2 = matchRepository.saveMatch(match2);

    List<Match> foundMatches = matchRepository.findAllMatches();

    Assertions.assertEquals(2, foundMatches.size());
  }

  @Test
  public void ensureFindAllMatchesReturnsForNonExistingMatchesEmptyList() {
    var foundMatches = matchRepository.findAllMatches();

    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureFindMatchByIdForExistingMatchReturnsMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new User(null, "Tesname", "LastName", false));
    Match match = new Match(null, savedGame.get(), List.of(savedUser.get()));
    Optional<Match> savedMatch1 = matchRepository.saveMatch(match);

    var foundMatch = matchRepository.findMatchById(savedMatch1.get().getId());

    Assertions.assertTrue(foundMatch.isPresent());
    Assertions.assertEquals(match.getGame().getName(), foundMatch.get().getGame().getName());
  }

  @Test
  public void ensureFindMatchByIdForNonExistingMatchReturnsEmptyOptional() {

    var foundMatch = matchRepository.findMatchById(1L);
    Assertions.assertTrue(foundMatch.isEmpty());
  }

  @Test
  public void ensureDeleteMatchByIdForExistingMatchWorks() {
    Optional<Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new User(null, "Tesname", "LastName", false));
    Match match = new Match(null, savedGame.get(), List.of(savedUser.get()));

    Optional<Match> savedMatch = matchRepository.saveMatch(match);

    matchRepository.deleteMatch(savedMatch.get().getId());

    Assertions.assertFalse(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureUpdateMatchForExistingMatchReturnsUpdatedMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new User(null, "Tesname", "LastName", false));

    Optional<User> anotherSavedUser = userRepository.saveUser(
        new User(null, "Tesname1", "LastName", false));

    Optional<Match> oldSavedMatch = matchRepository.saveMatch(
        new Match(null, savedGame.get(), List.of(savedUser.get())));
    Optional<Match> newUpdatedMatch = matchRepository.updateMatch(
        new Match(oldSavedMatch.get().getId(), savedGame.get(),
            List.of(savedUser.get(), anotherSavedUser.get())));

    Assertions.assertNotEquals(oldSavedMatch.get().getUsers().size(),
        newUpdatedMatch.get().getUsers().size());
    Assertions.assertEquals(oldSavedMatch.get().getId(), newUpdatedMatch.get().getId());
  }

  @Test
  public void ensureUpdateMatchForNonExistingMatchThrowsIllegalArgumentException() {
    Optional<Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new User(null, "Tesname", "LastName", false));

    Assertions.assertTrue(matchRepository.updateMatch(
        new Match(10000L, savedGame.get(), List.of(savedUser.get()))).isEmpty());
    Assertions.assertFalse(matchRepository.existsMatchById(10000L));

  }
}
