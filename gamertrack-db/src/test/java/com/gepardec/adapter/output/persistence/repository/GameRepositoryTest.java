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
class GameRepositoryTest extends GamertrackDbIT {

  @BeforeEach
  void beforeEach() throws Exception {
    super.removeTableData(GameEntity.class);
  }

  @Inject
  private GameRepository repository;


  @Test
  void ensureWriteAndReadValidGameWorks() {
    Game game = TestFixtures.game(null);

    var savedGame = repository.saveGame(game);
    Assertions.assertNotNull(savedGame);
    Assertions.assertEquals(game.getTitle(), savedGame.get().getTitle());
    Assertions.assertEquals(game.getRules(), savedGame.get().getRules());
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
        () -> repository.deleteGame(notExisingGame.getId()));
  }

  @Test
  void ensureUpdateValidGameWorks() {
    Game oldGame = TestFixtures.game(null);
    var persistedOldGame = repository.saveGame(oldGame);

    System.out.println(persistedOldGame.get().getId());

    Game newGame = new Game(persistedOldGame.get().getId(), "NewTitleTest", "NewRules");

    System.out.println(newGame.getId());
    var persistedUpdatedGame = repository.updateGame(newGame);
    System.out.println(persistedUpdatedGame.get());
    Assertions.assertTrue(persistedUpdatedGame.isPresent());
    Assertions.assertEquals(newGame.getTitle(), persistedUpdatedGame.get().getTitle());
    Assertions.assertEquals(newGame.getRules(), persistedUpdatedGame.get().getRules());
  }

  @Test
  void ensureUpdateNotExistingGameReturnsOptionalEmpty() {
    Game newGame = new Game(100000L, "NewTitle", "NewRules");
    var persistedUpdatedGame = repository.updateGame(newGame);

    Assertions.assertTrue(persistedUpdatedGame.isEmpty());
  }

  @Test
  void ensureFindGameByIdForExistingGameWorksReturnsGame() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var persistedGame = repository.saveGame(game);

    var foundGame = repository.findGameById(persistedGame.get().getId());

    Assertions.assertTrue(foundGame.isPresent());
    Assertions.assertEquals(game.getTitle(), foundGame.get().getTitle());
    Assertions.assertEquals(game.getRules(), foundGame.get().getRules());
  }

  @Test
  void ensureFindGameByIdForNotExistingGameReturnsOptionalEmpty() {
    Game notExistingGame = TestFixtures.gameToGameDto(TestFixtures.game());

    var foundGame = repository.findGameById(notExistingGame.getId());
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
        .map(Game::getTitle)
        .toList()
        .containsAll(gameDtos
            .stream()
            .map(Game::getTitle)
            .toList()));
  }

  @Test
  void ensureFindAllGamesForNoExistingGamesReturnsEmptyList() {
    Assertions.assertTrue(repository.findAllGames().isEmpty());
  }

  @Test
  void ensureGameExistsByGameNameForExistingGameReturnsGame() {
    Game game = TestFixtures.game(null);
    game.setTitle("NewTitle");

    var savedGame = repository.saveGame(TestFixtures.gameToGameDto(game));

    boolean existsByGameName = repository.gameExistsByGameName(savedGame.get().getTitle());

    Assertions.assertTrue(savedGame.isPresent());
    Assertions.assertEquals(savedGame.get().getTitle(), game.getTitle());
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

