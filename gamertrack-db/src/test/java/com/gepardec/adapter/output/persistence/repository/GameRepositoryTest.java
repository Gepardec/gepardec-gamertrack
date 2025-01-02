package com.gepardec.adapter.output.persistence.repository;

import static com.gepardec.TestFixtures.game;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class GameRepositoryTest extends GamertrackDbIT {

  @BeforeEach
  void beforeEach() throws Exception {
    super.removeTableData(GameEntity.class);
  }

  @Inject
  private GameRepository repository;


  @Test
  void ensureWriteAndReadValidGameWorks() {
    Game game = game(null);

    var savedGame = repository.saveGame(game);
    Assertions.assertNotNull(savedGame);
    Assertions.assertEquals(game.getName(), savedGame.get().getName());
    Assertions.assertEquals(game.getRules(), savedGame.get().getRules());
  }

  @Test
  void ensureSavingInvalidGameThrowsConstrainViolationException() {
    Game invalidGame = new Game(null, null, "", "TestGame");

    Assertions.assertThrows(ConstraintViolationException.class,
        () -> repository.saveGame(invalidGame));
  }

  @Test
  void ensureDeleteExistingGameWorks() {
    Game game = game(null);
    var alreadyExistingGame = repository.saveGame(game);

    repository.deleteGame(alreadyExistingGame.get().getId());

    Assertions.assertTrue(
        repository.findGameByToken(alreadyExistingGame.get().getToken()).isEmpty());
  }

  @Test
  void ensureDeleteInvalidGameThrowsIllegalArgumentException() {
    Game notExisingGame = game();

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> repository.deleteGame(notExisingGame.getId()));
  }

  @Test
  void ensureUpdateValidGameWorks() {
    Game oldGame = game(null);
    var persistedOldGame = repository.saveGame(oldGame);

    Game newGame = game(persistedOldGame.get().getId());
    newGame.setName("New Name");

    var persistedUpdatedGame = repository.updateGame(newGame);
    Assertions.assertTrue(persistedUpdatedGame.isPresent());
    Assertions.assertEquals(newGame.getName(), persistedUpdatedGame.get().getName());
    Assertions.assertEquals(newGame.getRules(), persistedUpdatedGame.get().getRules());
  }

  @Test
  void ensureUpdateNotExistingGameReturnsOptionalEmpty() {
    Game newGame = game(10000L);
    var persistedUpdatedGame = repository.updateGame(newGame);

    Assertions.assertTrue(persistedUpdatedGame.isEmpty());
  }

  @Test
  void ensureFindGameByIdForExistingGameWorksReturnsGame() {
    Game game = game(null);
    var persistedGame = repository.saveGame(game);

    var foundGame = repository.findGameById(persistedGame.get().getId());

    Assertions.assertTrue(foundGame.isPresent());
    Assertions.assertEquals(game.getName(), foundGame.get().getName());
    Assertions.assertEquals(game.getRules(), foundGame.get().getRules());
  }

  @Test
  void ensureFindGameByTokenForNotExistingGameReturnsOptionalEmpty() {
    Game notExistingGame = game();

    var foundGame = repository.findGameByToken(notExistingGame.getToken());
    Assertions.assertTrue(foundGame.isEmpty());
  }

  @Test
  void ensureFindAllGamesForExistingGamesReturnsAllGames() {
    List<Game> games = TestFixtures.games(10);
    List<Game> gameDtos = games.stream()
        .peek(repository::saveGame)
        .toList();

    var foundGames = repository.findAllGames();

    Assertions.assertEquals(games.size(), foundGames.size());
    Assertions.assertTrue(foundGames
        .stream()
        .map(Game::getName)
        .toList()
        .containsAll(gameDtos
            .stream()
            .map(Game::getName)
            .toList()));
  }

  @Test
  void ensureFindAllGamesForNoExistingGamesReturnsEmptyList() {
    Assertions.assertTrue(repository.findAllGames().isEmpty());
  }

  @Test
  void ensureGameExistsByGameNameForExistingGameReturnsGame() {
    Game game = game(null);
    game.setName("NewTitle");

    var savedGame = repository.saveGame(game);

    boolean existsByGameName = repository.gameExistsByGameName(savedGame.get().getName());

    Assertions.assertTrue(savedGame.isPresent());
    Assertions.assertEquals(savedGame.get().getName(), game.getName());
    Assertions.assertTrue(existsByGameName);
  }

  @Test
  void ensureGameExistsByGameNameForNotExistingGameReturnsFalse() {

    boolean existsByGameName = repository.gameExistsByGameName("NotExistingName");
    Assertions.assertFalse(existsByGameName);
  }

  @Test
  void ensureExistsByGameIdForExistingGameReturnsGame() {
    Game game = game(null);
    var savedGame = repository.saveGame(game);

    boolean existsByGameId = repository.existsByGameToken(savedGame.get().getToken());

    Assertions.assertTrue(existsByGameId);
  }

  @Test
  void ensureExistsByGameIdForNotExistingGameReturnsFalse() {
    boolean existsByGameId = repository.existsByGameToken(game().getToken());

    Assertions.assertFalse(existsByGameId);
  }

}

