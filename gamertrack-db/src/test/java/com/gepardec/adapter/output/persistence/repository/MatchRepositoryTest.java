package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static com.gepardec.TestFixtures.*;

@io.quarkus.test.junit.QuarkusTest
public class MatchRepositoryTest  {

  @Inject
  private MatchRepository matchRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GameRepository gameRepository;


  @BeforeEach
  public void before() throws Exception {
    
  }

  @Test
  public void ensureSaveAndReadMatchWorks() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser = userRepository.saveUser(users(1).getFirst());
    Match match = match(null);
    match.setGame(savedGame.get());
    match.setUsers(List.of(savedUser.get()));

    var savedAndReadMatch = matchRepository.saveMatch(match);

    Assertions.assertTrue(savedAndReadMatch.isPresent());
    Assertions.assertEquals(match.getToken(), savedAndReadMatch.get().getToken());
    Assertions.assertEquals(match.getGame().getName(), savedAndReadMatch.get().getGame().getName());
    Assertions.assertEquals(match.getUsers().getFirst().getFirstname(),
        savedAndReadMatch.get().getUsers().getFirst().getFirstname());
  }

  @Test
  public void ensureSavingMatchWithInvalidReferencesFails() {
    Match match = match(null, game(2L), List.of(user(2L)));

    Assertions.assertThrows(PersistenceException.class,
        () -> matchRepository.saveMatch(match));
  }

  @Test
  public void ensureFindAllMatchesReturnsForExistingMatchesAllMatches() {
    Optional<Game> savedGame1 = gameRepository.saveGame(game(null));
    Optional<Game> savedGame2 = gameRepository.saveGame(games(1).getFirst());

    List<User> users = users(2);
    Optional<User> savedUser1 = userRepository.saveUser(users.getFirst());
    Optional<User> savedUser2 = userRepository.saveUser(users.getLast());

    Match match1 = match(null, savedGame1.get(), List.of(savedUser1.get()));
    Match match2 = match(null, savedGame2.get(), List.of(savedUser2.get()));

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
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser = userRepository.saveUser(user(null));
    Match match = match(null, savedGame.get(), List.of(savedUser.get()));
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
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser = userRepository.saveUser(user(null));
    Match match = match(null, savedGame.get(), List.of(savedUser.get()));

    Optional<Match> savedMatch = matchRepository.saveMatch(match);

    matchRepository.deleteMatch(savedMatch.get().getId());

    Assertions.assertFalse(matchRepository.existsMatchById(savedMatch.get().getId()));
  }

  @Test
  public void ensureUpdateMatchForExistingMatchReturnsUpdatedMatch() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser = userRepository.saveUser(user(null));

    Optional<User> anotherSavedUser = userRepository.saveUser(users(1).getFirst());

    Optional<Match> oldSavedMatch = matchRepository.saveMatch(
        match(null, savedGame.get(), List.of(savedUser.get())));
    Optional<Match> newUpdatedMatch = matchRepository.updateMatch(
        match(oldSavedMatch.get().getId(), savedGame.get(),
            List.of(savedUser.get(), anotherSavedUser.get())));

    Assertions.assertNotEquals(oldSavedMatch.get().getUsers().size(),
        newUpdatedMatch.get().getUsers().size());
    Assertions.assertEquals(oldSavedMatch.get().getId(), newUpdatedMatch.get().getId());
  }

  @Test
  public void ensureUpdateMatchForNonExistingMatchThrowsIllegalArgumentException() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser = userRepository.saveUser(user(null));

    Assertions.assertTrue(matchRepository.updateMatch(
        match(10000L, savedGame.get(), List.of(savedUser.get()))).isEmpty());
    Assertions.assertFalse(matchRepository.existsMatchById(10000L));

  }

  @Test
  public void countMatchesFilteredAndUnfilteredReturnsCorrectCount() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser1 = userRepository.saveUser(user(null));
    Optional<User> savedUser2 = userRepository.saveUser(user(null));
    Optional<User> savedUser3 = userRepository.saveUser(user(null));

    Match match1 = match(null, savedGame.get(), List.of(savedUser1.get(), savedUser2.get()));
    matchRepository.saveMatch(match1);
    Match match2 = match(null, savedGame.get(), List.of(savedUser2.get()));
    matchRepository.saveMatch(match2);
    Match match3 = match(null, savedGame.get(), List.of(savedUser3.get()));
    matchRepository.saveMatch(match3);

    long countFilteredWithUser1AndGame = matchRepository.countMatchesFilteredAndUnfiltered(savedGame.get().getToken(),
            savedUser1.get().getToken());
    long countFilteredWithUser2AndGame = matchRepository.countMatchesFilteredAndUnfiltered(savedGame.get().getToken(),
            savedUser2.get().getToken());

    long countFilteredWithGameOnly = matchRepository.countMatchesFilteredAndUnfiltered(savedGame.get().getToken(),
            null);
    long countFilteredWithUser2only = matchRepository.countMatchesFilteredAndUnfiltered(null,
            savedUser2.get().getToken());

    long countAllMatches = matchRepository.countMatchesFilteredAndUnfiltered(null, null);

    Assertions.assertEquals(1, countFilteredWithUser1AndGame);
    Assertions.assertEquals(2, countFilteredWithUser2AndGame);
    Assertions.assertEquals(3, countFilteredWithGameOnly);
    Assertions.assertEquals(2, countFilteredWithUser2only);
    Assertions.assertEquals(3, countAllMatches);
  }

  @Test
  void ensureFindAllMatchesOrFilteredByGameTokenAndUserTokenReturnsAllMatchesForNoFilter() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser1 = userRepository.saveUser(user(null));
    Optional<User> savedUser2 = userRepository.saveUser(user(null));

    var match1 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser1.get(), savedUser2.get()))).get();
    var match2 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser2.get()))).get();

    List<Match> foundMatches = matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(null, null, PageRequest.ofPage(1));


    Assertions.assertEquals(2, foundMatches.size());
    Assertions.assertTrue(foundMatches.containsAll(List.of(match1, match2)));
  }

  @Test
  void ensureFindAllMatchesOrFilteredByGameTokenAndUserTokenReturnsFilteredMatchesForGameToken() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser1 = userRepository.saveUser(user(null));
    Optional<User> savedUser2 = userRepository.saveUser(user(null));

    var match1 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser1.get(), savedUser2.get()))).get();
    var match2 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser2.get()))).get();

    List<Match> foundMatches = matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(savedGame.get().getToken(),
            null, PageRequest.ofPage(1));

    Assertions.assertEquals(2, foundMatches.size());
    Assertions.assertTrue(foundMatches.containsAll(List.of(match1, match2)));
  }

  @Test
  void ensureFindAllMatchesOrFilteredByGameTokenAndUserTokenReturnsFilteredMatchesForUserToken() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser1 = userRepository.saveUser(user(null));
    Optional<User> savedUser2 = userRepository.saveUser(user(null));

    var match1 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser1.get(),
            savedUser2.get()))).get();
    var match2 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser2.get()))).get();

    List<Match> foundMatches = matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(null,
            savedUser1.get().getToken(), PageRequest.ofPage(1));

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertTrue(foundMatches.contains(match1));
  }

  @Test
  void ensureFindAllMatchesOrFilteredByGameTokenAndUserTokenReturnsFilteredMatchesForGameTokenAndUserToken() {
    Optional<Game> savedGame = gameRepository.saveGame(game(null));
    Optional<User> savedUser1 = userRepository.saveUser(user(null));
    Optional<User> savedUser2 = userRepository.saveUser(user(null));

    var match1 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser1.get(),
            savedUser2.get()))).get();
    var match2 = matchRepository.saveMatch(match(null, savedGame.get(), List.of(savedUser2.get()))).get();

    List<Match> foundMatches = matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(savedGame.get().getToken(),
            savedUser1.get().getToken(), PageRequest.ofPage(1));

    Assertions.assertEquals(1, foundMatches.size());
    Assertions.assertTrue(foundMatches.contains(match1));
  }

}
