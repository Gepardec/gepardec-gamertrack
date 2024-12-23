package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
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
    removeTableData(com.gepardec.adapter.output.persistence.entity.Match.class, com.gepardec.adapter.output.persistence.entity.Game.class, com.gepardec.adapter.output.persistence.entity.User.class);
  }

  @Test
  public void ensureSaveAndReadMatchWorks() {
    com.gepardec.adapter.output.persistence.entity.Match match = new com.gepardec.adapter.output.persistence.entity.Match();
    com.gepardec.adapter.output.persistence.entity.Game game = new com.gepardec.adapter.output.persistence.entity.Game();
    game.setName("testname");
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(
        new Game(null, game.getName(), game.getRules()));
    match.setGame(savedGame.get());
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));
    match.setUsers(List.of(savedUser.get()));
    Match matchDto = TestFixtures.matchToMatchDto(match);

    var savedAndReadMatch = matchRepository.saveMatch(matchDto);

    Assertions.assertTrue(savedAndReadMatch.isPresent());
    Assertions.assertEquals(match.getGame().getName(), savedAndReadMatch.get().getGame().getName());
    Assertions.assertEquals(match.getUsers().getFirst().getFirstname(),
        savedAndReadMatch.get().getUsers().getFirst().getFirstname());
  }

  @Test
  public void ensureSavingMatchWithInvalidReferencesFails() {
    Match match = new Match(null, 4000L, List.of(2000L));

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> matchRepository.saveMatch(match));
  }

  @Test
  public void ensureFindAllMatchesReturnsForExistingMatchesAllMatches() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame1 = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame2 = gameRepository.saveGame(new Game(null, "TestName2", "No"));

    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser1 = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser2 = userRepository.saveUser(
        new User(0, "Tesname", "No", false));

    com.gepardec.adapter.output.persistence.entity.Match match1 = new com.gepardec.adapter.output.persistence.entity.Match(savedGame1.get(), List.of(savedUser1.get()));
    com.gepardec.adapter.output.persistence.entity.Match match2 = new com.gepardec.adapter.output.persistence.entity.Match(savedGame2.get(), List.of(savedUser2.get()));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch1 = matchRepository.saveMatch(new Match(match1));
    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch2 = matchRepository.saveMatch(new Match(match2));

    List<com.gepardec.adapter.output.persistence.entity.Match> foundMatches = matchRepository.findAllMatches();

    Assertions.assertEquals(2, foundMatches.size());
  }

  @Test
  public void ensureFindAllMatchesReturnsForNonExistingMatchesEmptyList() {
    var foundMatches = matchRepository.findAllMatches();

    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureFindMatchByIdForExistingMatchReturnsMatch() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));
    com.gepardec.adapter.output.persistence.entity.Match match = new com.gepardec.adapter.output.persistence.entity.Match(savedGame.get(), List.of(savedUser.get()));
    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch1 = matchRepository.saveMatch(new Match(match));

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
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));
    com.gepardec.adapter.output.persistence.entity.Match match = new com.gepardec.adapter.output.persistence.entity.Match(savedGame.get(), List.of(savedUser.get()));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch = matchRepository.saveMatch(new Match(match));

    matchRepository.deleteMatch(savedMatch.get().getId());

    Assertions.assertFalse(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureUpdateMatchForExistingMatchReturnsUpdatedMatch() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));

    Optional<com.gepardec.adapter.output.persistence.entity.User> anotherSavedUser = userRepository.saveUser(
        new User(0, "Tesname1", "LastName", false));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> oldSavedMatch = matchRepository.saveMatch(
        new Match(null, savedGame.get().getId(), List.of(savedUser.get().getId())));
    Optional<com.gepardec.adapter.output.persistence.entity.Match> newUpdatedMatch = matchRepository.updateMatch(
        new Match(oldSavedMatch.get().getId(), savedGame.get()
            .getId(), List.of(savedUser.get().getId(), anotherSavedUser.get().getId())));

    Assertions.assertNotEquals(oldSavedMatch.get().getUsers().size(),
        newUpdatedMatch.get().getUsers().size());
    Assertions.assertEquals(oldSavedMatch.get().getId(), newUpdatedMatch.get().getId());
  }

  @Test
  public void ensureUpdateMatchForNonExistingMatchReturnsEmptyOptional() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));

    Assertions.assertTrue(matchRepository.updateMatch(
        new Match(10000L, savedGame.get().getId(), List.of(savedUser.get().getId()))).isEmpty());
    Assertions.assertFalse(matchRepository.existsMatchById(10000L));

  }

  @Test
  public void ensureFindMatchByGameIdForExistingMatchReturnsMatch() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch = matchRepository.saveMatch(
        new Match(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    List<com.gepardec.adapter.output.persistence.entity.Match> foundMatches = matchRepository.findMatchByGameId(
        savedMatch.get().getGame().getId());

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertEquals(savedMatch.get().getId(), foundMatches.get(0).getId());
  }

  @Test
  public void ensureFindMatchByGameIdForNonExistingMatchReturnsEmptyList() {

    List<com.gepardec.adapter.output.persistence.entity.Match> foundMatches = matchRepository.findMatchByGameId(1L);
    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureFindMatchByUserIdForExistingMatchReturnsMatch() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch = matchRepository.saveMatch(
        new Match(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    List<com.gepardec.adapter.output.persistence.entity.Match> foundMatches = matchRepository.findMatchByUserId(savedUser.get().getId());

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertEquals(savedMatch.get().getId(), foundMatches.get(0).getId());

  }


  @Test
  public void ensureFindMatchByUserIdForNonExistingMatchReturnsEmptyList() {
    List<com.gepardec.adapter.output.persistence.entity.Match> foundMatches = matchRepository.findMatchByUserId(1L);
    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureExistsMatchByIdForExistingMatchReturnsTrue() {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> savedGame = gameRepository.saveGame(new Game(null, "TestName", "No"));
    Optional<com.gepardec.adapter.output.persistence.entity.User> savedUser = userRepository.saveUser(
        new User(0, "Tesname", "LastName", false));

    Optional<com.gepardec.adapter.output.persistence.entity.Match> savedMatch = matchRepository.saveMatch(
        new Match(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    Assertions.assertTrue(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureExistsMatchByIdForNonExistingMatchReturnsFalse() {
    Assertions.assertFalse(matchRepository.existsMatchById(1L));
  }
}
