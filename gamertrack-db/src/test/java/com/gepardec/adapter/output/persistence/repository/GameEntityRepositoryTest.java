package com.gepardec.adapter.output.persistence.repository;

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
class GameEntityRepositoryTest extends GamertrackDbIT {

  @BeforeEach
  void beforeEach() throws Exception {
    super.removeTableData(GameEntity.class);
  }

  @Inject
  private GameRepository repository;


  @Test
  void ensureWriteAndReadValidGameWorks() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game(null));

    var savedGame = repository.saveGame(game);
    Assertions.assertNotNull(savedGame);
    Assertions.assertEquals(game.title(), savedGame.get().getName());
    Assertions.assertEquals(game.rules(), savedGame.get().getRules());
  }

  @Test
  void ensureSavingInvalidGameThrowsConstrainViolationException() {
    Game invalidGame = new Game(null, "", "TestGame");

    Assertions.assertThrows(ConstraintViolationException.class,
        () -> repository.saveGame(invalidGame));
  }

  @Test
  void ensureDeleteExistingGameWorks() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var alreadyExistingGame = repository.saveGame(game);

    repository.deleteGame(alreadyExistingGame.get().getId());

    Assertions.assertTrue(repository.findGameById(alreadyExistingGame.get().getId()).isEmpty());
  }

  @Test
  void ensureDeleteInvalidGameThrowsIllegalArgumentException() {
    Game notExisingGame = TestFixtures.gameToGameDto(TestFixtures.game());

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> repository.deleteGame(notExisingGame.id()));
  }

  @Test
  void ensureUpdateValidGameWorks() {
    Game oldGame = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var persistedOldGame = repository.saveGame(oldGame);

    Game newGame = new Game(persistedOldGame.get().getId(), "NewTitle", "NewRules");

    var persistedUpdatedGame = repository.updateGame(newGame);
    Assertions.assertTrue(persistedUpdatedGame.isPresent());
    Assertions.assertEquals(newGame.title(), persistedUpdatedGame.get().getName());
    Assertions.assertEquals(newGame.rules(), persistedUpdatedGame.get().getRules());
  }

  @Test
  void ensureUpdateNotExistingGameReturnsOptionalEmpty() {
    Game newGame = new Game(100L, "NewTitle", "NewRules");
    var persistedUpdatedGame = repository.updateGame(newGame);

    Assertions.assertTrue(persistedUpdatedGame.isEmpty());
  }

  @Test
  void ensureFindGameByIdForExistingGameWorksReturnsGame() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var persistedGame = repository.saveGame(game);

    var foundGame = repository.findGameById(persistedGame.get().getId());

    Assertions.assertTrue(foundGame.isPresent());
    Assertions.assertEquals(game.title(), foundGame.get().getName());
    Assertions.assertEquals(game.rules(), foundGame.get().getRules());
  }

  @Test
  void ensureFindGameByIdForNotExistingGameReturnsOptionalEmpty() {
    Game notExistingGame = TestFixtures.gameToGameDto(TestFixtures.game());

    var foundGame = repository.findGameById(notExistingGame.id());
    Assertions.assertTrue(foundGame.isEmpty());
  }

  @Test
  void ensureFindAllGamesForExistingGamesReturnsAllGames() {
    List<GameEntity> games = TestFixtures.games(10);
    List<Game> gameDtos = games.stream()
        .map(TestFixtures::gameToGameDto)
        .peek(repository::saveGame)
        .toList();

    var foundGames = repository.findAllGames();

    Assertions.assertEquals(games.size(), foundGames.size());
    Assertions.assertTrue(foundGames
        .stream()
        .map(GameEntity::getName)
        .toList()
        .containsAll(gameDtos
            .stream()
            .map(Game::title)
            .toList()));
  }

  @Test
  void ensureFindAllGamesForNoExistingGamesReturnsEmptyList() {
    Assertions.assertTrue(repository.findAllGames().isEmpty());
  }

  @Test
  void ensureGameExistsByGameNameForExistingGameReturnsGame() {
    GameEntity game = TestFixtures.game(null);
    game.setName("NewTitle");

    var savedGame = repository.saveGame(TestFixtures.gameToGameDto(game));

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
    Game game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var savedGame = repository.saveGame(game);

    boolean existsByGameId = repository.existsByGameId(savedGame.get().getId());

    Assertions.assertTrue(existsByGameId);
  }

  @Test
  void ensureExistsByGameIdForNotExistingGameReturnsFalse() {
    boolean existsByGameId = repository.existsByGameId(200000L);

    Assertions.assertFalse(existsByGameId);
  }

}

