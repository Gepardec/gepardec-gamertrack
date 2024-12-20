package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import com.gepardec.model.dto.GameDto;
import com.gepardec.model.dto.MatchDto;
import com.gepardec.model.dto.UserDto;
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
    removeTableData(Match.class, Game.class, User.class);
  }

  @Test
  public void ensureSaveAndReadMatchWorks() {
    Match match = new Match();
    Game game = new Game();
    game.setName("testname");
    Optional<Game> savedGame = gameRepository.saveGame(
        new GameDto(null, game.getName(), game.getRules()));
    match.setGame(savedGame.get());
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));
    match.setUsers(List.of(savedUser.get()));
    MatchDto matchDto = TestFixtures.matchToMatchDto(match);

    var savedAndReadMatch = matchRepository.saveMatch(matchDto);

    Assertions.assertTrue(savedAndReadMatch.isPresent());
    Assertions.assertEquals(match.getGame().getName(), savedAndReadMatch.get().getGame().getName());
    Assertions.assertEquals(match.getUsers().getFirst().getFirstname(),
        savedAndReadMatch.get().getUsers().getFirst().getFirstname());
  }

  @Test
  public void ensureSavingMatchWithInvalidReferencesFails() {
    MatchDto match = new MatchDto(null, 4000L, List.of(2000L));

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> matchRepository.saveMatch(match));
  }

  @Test
  public void ensureFindAllMatchesReturnsForExistingMatchesAllMatches() {
    Optional<Game> savedGame1 = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<Game> savedGame2 = gameRepository.saveGame(new GameDto(null, "TestName2", "No"));

    Optional<User> savedUser1 = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));
    Optional<User> savedUser2 = userRepository.saveUser(
        new UserDto(0, "Tesname", "No", false));

    Match match1 = new Match(savedGame1.get(), List.of(savedUser1.get()));
    Match match2 = new Match(savedGame2.get(), List.of(savedUser2.get()));

    Optional<Match> savedMatch1 = matchRepository.saveMatch(new MatchDto(match1));
    Optional<Match> savedMatch2 = matchRepository.saveMatch(new MatchDto(match2));

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
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));
    Match match = new Match(savedGame.get(), List.of(savedUser.get()));
    Optional<Match> savedMatch1 = matchRepository.saveMatch(new MatchDto(match));

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
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));
    Match match = new Match(savedGame.get(), List.of(savedUser.get()));

    Optional<Match> savedMatch = matchRepository.saveMatch(new MatchDto(match));

    matchRepository.deleteMatch(savedMatch.get().getId());

    Assertions.assertFalse(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureUpdateMatchForExistingMatchReturnsUpdatedMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));

    Optional<User> anotherSavedUser = userRepository.saveUser(
        new UserDto(0, "Tesname1", "LastName", false));

    Optional<Match> oldSavedMatch = matchRepository.saveMatch(
        new MatchDto(null, savedGame.get().getId(), List.of(savedUser.get().getId())));
    Optional<Match> newUpdatedMatch = matchRepository.updateMatch(
        new MatchDto(oldSavedMatch.get().getId(), savedGame.get()
            .getId(), List.of(savedUser.get().getId(), anotherSavedUser.get().getId())));

    Assertions.assertNotEquals(oldSavedMatch.get().getUsers().size(),
        newUpdatedMatch.get().getUsers().size());
    Assertions.assertEquals(oldSavedMatch.get().getId(), newUpdatedMatch.get().getId());
  }

  @Test
  public void ensureUpdateMatchForNonExistingMatchReturnsEmptyOptional() {
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));

    Assertions.assertTrue(matchRepository.updateMatch(
        new MatchDto(10000L, savedGame.get().getId(), List.of(savedUser.get().getId()))).isEmpty());
    Assertions.assertFalse(matchRepository.existsMatchById(10000L));

  }

  @Test
  public void ensureFindMatchByGameIdForExistingMatchReturnsMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));

    Optional<Match> savedMatch = matchRepository.saveMatch(
        new MatchDto(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    List<Match> foundMatches = matchRepository.findMatchByGameId(
        savedMatch.get().getGame().getId());

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertEquals(savedMatch.get().getId(), foundMatches.get(0).getId());
  }

  @Test
  public void ensureFindMatchByGameIdForNonExistingMatchReturnsEmptyList() {

    List<Match> foundMatches = matchRepository.findMatchByGameId(1L);
    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureFindMatchByUserIdForExistingMatchReturnsMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));

    Optional<Match> savedMatch = matchRepository.saveMatch(
        new MatchDto(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    List<Match> foundMatches = matchRepository.findMatchByUserId(savedUser.get().getId());

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertEquals(savedMatch.get().getId(), foundMatches.get(0).getId());

  }


  @Test
  public void ensureFindMatchByUserIdForNonExistingMatchReturnsEmptyList() {
    List<Match> foundMatches = matchRepository.findMatchByUserId(1L);
    Assertions.assertTrue(foundMatches.isEmpty());
  }

  @Test
  public void ensureExistsMatchByIdForExistingMatchReturnsTrue() {
    Optional<Game> savedGame = gameRepository.saveGame(new GameDto(null, "TestName", "No"));
    Optional<User> savedUser = userRepository.saveUser(
        new UserDto(0, "Tesname", "LastName", false));

    Optional<Match> savedMatch = matchRepository.saveMatch(
        new MatchDto(null, savedGame.get().getId(), List.of(savedUser.get().getId())));

    Assertions.assertTrue(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureExistsMatchByIdForNonExistingMatchReturnsFalse() {
    Assertions.assertFalse(matchRepository.existsMatchById(1L));
  }
}
