package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.model.Game;
import com.gepardec.model.dto.GameDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class GameRepositoryTest extends GamertrackDbIT {


  @Deployment
  public static Archive<?> createDeployment() {
    return ShrinkWrap.create(JavaArchive.class)
        .addPackage(GameRepositoryImpl.class.getPackage())
        .addPackage(GameDto.class.getPackage())
        .addPackage(Game.class.getPackage())
        .addPackage(GameRepository.class.getPackage())
        .addPackage(ConstraintViolationException.class.getPackage())
        .addClass(GameRepositoryImpl.class)
        .addClass(GameDto.class)
        .addClass(Game.class)
        .addClass(TestFixtures.class)
        .addClass(ConstraintViolationException.class)
        .addClass(Mapper.class)
        .addAsManifestResource("beans.xml", "beans.xml")
        .addAsManifestResource("persistence.xml", "persistence.xml");

  }

  @BeforeEach
  void beforeEach() throws Exception {
    super.removeTableData(em, utx, Game.class);
  }

  @Inject
  private GameRepository repository;

  @PersistenceContext
  private EntityManager em;

  @Inject
  private UserTransaction utx;


  @Test
  void ensureWriteAndReadValidGameWorks() {
    GameDto game = TestFixtures.gameToGameDto(TestFixtures.game(null));

    var savedGame = repository.saveGame(game);
    Assertions.assertNotNull(savedGame);
    Assertions.assertEquals(game.title(), savedGame.get().getName());
    Assertions.assertEquals(game.rules(), savedGame.get().getRules());
  }

  @Test
  void ensureSavingInvalidGameThrowsConstrainViolationException() {
    GameDto invalidGame = new GameDto(null, "", "TestGame");

    Assertions.assertThrows(ConstraintViolationException.class,
        () -> repository.saveGame(invalidGame));
  }

  @Test
  void ensureDeleteExistingGameWorks() {
    GameDto game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var alreadyExistingGame = repository.saveGame(game);

    repository.deleteGame(alreadyExistingGame.get().getId());

    Assertions.assertTrue(repository.findGameById(alreadyExistingGame.get().getId()).isEmpty());
  }

  @Test
  void ensureDeleteInvalidGameThrowsIllegalArgumentException() {
    GameDto notExisingGame = TestFixtures.gameToGameDto(TestFixtures.game());

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> repository.deleteGame(notExisingGame.id()));
  }

  @Test
  void ensureUpdateValidGameWorks() {
    GameDto oldGame = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var persistedOldGame = repository.saveGame(oldGame);

    GameDto newGame = new GameDto(persistedOldGame.get().getId(), "NewTitle", "NewRules");

    var persistedUpdatedGame = repository.updateGame(newGame);
    Assertions.assertTrue(persistedUpdatedGame.isPresent());
    Assertions.assertEquals(newGame.title(), persistedUpdatedGame.get().getName());
    Assertions.assertEquals(newGame.rules(), persistedUpdatedGame.get().getRules());
  }

  @Test
  void ensureUpdateNotExistingGameReturnsOptionalEmpty() {
    GameDto newGame = new GameDto(100L, "NewTitle", "NewRules");
    var persistedUpdatedGame = repository.updateGame(newGame);

    Assertions.assertTrue(persistedUpdatedGame.isEmpty());
  }

  @Test
  void ensureFindGameByIdForExistingGameWorksReturnsGame() {
    GameDto game = TestFixtures.gameToGameDto(TestFixtures.game(null));
    var persistedGame = repository.saveGame(game);

    var foundGame = repository.findGameById(persistedGame.get().getId());

    Assertions.assertTrue(foundGame.isPresent());
    Assertions.assertEquals(game.title(), foundGame.get().getName());
    Assertions.assertEquals(game.rules(), foundGame.get().getRules());
  }

  @Test
  void ensureFindGameByIdForNotExistingGameReturnsOptionalEmpty() {
    GameDto notExistingGame = TestFixtures.gameToGameDto(TestFixtures.game());

    var foundGame = repository.findGameById(notExistingGame.id());
    Assertions.assertTrue(foundGame.isEmpty());
  }

  @Test
  void ensureFindAllGamesForExistingGamesReturnsAllGames() {
    List<Game> games = TestFixtures.games(10);
    List<GameDto> gameDtos = games.stream()
        .map(TestFixtures::gameToGameDto)
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
            .map(GameDto::title)
            .toList()));
  }

  @Test
  void ensureFindAllGamesForNoExistingGamesReturnsEmptyList() {
    Assertions.assertTrue(repository.findAllGames().isEmpty());
  }

  @Test
  void ensureGameExistsByGameNameForExistingGameReturnsGame() {
    Game game = TestFixtures.game(null);
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
    GameDto game = TestFixtures.gameToGameDto(TestFixtures.game(null));
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

