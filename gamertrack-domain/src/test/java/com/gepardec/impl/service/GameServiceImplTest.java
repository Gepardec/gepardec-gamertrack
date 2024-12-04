package com.gepardec.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

  @Mock
  GameRepository gameRepository;

  @InjectMocks
  GameServiceImpl gameService;


  @Test
  void ensureSavingValidGameWorksAndReturnsValidGame() {
    //Given
    Game game = TestFixtures.game();

    //When
    when(gameRepository.GameExistsByGameName(any())).thenReturn(false);
    when(gameRepository.saveGame(game)).thenReturn(Optional.of(game));
    var savedGame = gameService.saveGame(game);

    //Then
    assertEquals(savedGame.get(), game);
  }

  @Test
  void ensureSavingInvalidGameWorksAndReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.GameExistsByGameName(any())).thenReturn(false);
    assertFalse(gameService.saveGame(game).isPresent());
  }

  @Test
  void ensureSavingAlreadyExistingGameFailsAndReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.GameExistsByGameName(any())).thenReturn(true);
    assertFalse(gameService.saveGame(game).isPresent());
  }

  @Test
  void ensureDeletingExistingGameWorksAndReturnsDeletedGame() {
    Game game = TestFixtures.game();

    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.of(game));

    assertEquals(gameService.deleteGame(game.getId()).get(), game);

  }

  @Test
  void ensureDeletingNotExistingGameReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.empty());
    assertEquals(gameService.deleteGame(game.getId()), Optional.empty());
  }

  @Test
  void ensureUpdatingExistingGameWorksAndReturnsUpdatedGame() {
    //Given
    Game gameOld = TestFixtures.game();

    Game gameWithNewValues = TestFixtures.game();
    gameWithNewValues.setName("New Name");
    gameWithNewValues.setRules("New Rules");

    //When
    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.of(gameOld));
    when(gameRepository.saveGame(any())).thenReturn(Optional.of(gameWithNewValues));

    var updatedGame = gameService.updateGame(gameWithNewValues.getId(), gameWithNewValues);

    //Then

    assertNotEquals(gameOld, updatedGame);
    assertEquals(updatedGame.get(), gameWithNewValues);
  }

  @Test
  void ensureUpdatingNotExistingGameReturnsOptionalEmpty() {
    Game gameWithNewValues = TestFixtures.game();

    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.empty());

    assertEquals(gameService.updateGame(gameWithNewValues.getId(), gameWithNewValues),
        Optional.empty());
  }

  @Test
  void ensureFindGameByIdForExistingGameReturnsGame() {
    Game game = TestFixtures.game();
    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.of(game));
    assertEquals(gameService.findGameById(game.getId()).get(), game);
  }

  @Test
  void ensureFindGameByIdForNotExistingGameReturnsOptionalEmpty() {
    Game game = TestFixtures.game();
    when(gameRepository.findGameById(anyLong())).thenReturn(Optional.empty());

    assertEquals(gameService.findGameById(game.getId()), Optional.empty());
  }


  @Test
  void ensureFindAllGamesReturnsAllGames() {
    List<Game> games = TestFixtures.games(10);

    when(gameRepository.findAllGames()).thenReturn(games);
    assertEquals(gameService.findAllGames(), games);
    assertEquals(gameService.findAllGames().size(), games.size());
  }

  @Test
  void ensureFindAllGamesReturnsEmptyListIfNoGamesExist() {
    List<Game> games = TestFixtures.games(0);

    when(gameRepository.findAllGames()).thenReturn(games);
    assertEquals(0, gameService.findAllGames().size());
  }
}