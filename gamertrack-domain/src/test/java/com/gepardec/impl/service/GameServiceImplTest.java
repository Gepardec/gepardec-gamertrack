package com.gepardec.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

  @Mock
  GameRepository gameRepository;
  @Mock
  TokenService tokenService;
  @InjectMocks
  GameServiceImpl gameService;


  @Test
  void ensureSavingValidGameWorksAndReturnsValidGame() {
    //Given
    Game game = TestFixtures.game();

    when(tokenService.generateToken()).thenReturn(game.getToken());

    //When
    when(gameRepository.gameExistsByGameName(any())).thenReturn(false);
    when(gameRepository.saveGame(game)).thenReturn(Optional.of(game));
    var savedGame = gameService.saveGame(game);

    //Then
    assertEquals(savedGame.get(), game);
  }


  @Test
  void ensureSavingInvalidGameWorksAndReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.gameExistsByGameName(any())).thenReturn(false);
    assertFalse(gameService.saveGame(game).isPresent());
  }

  @Test
  void ensureSavingAlreadyExistingGameFailsAndReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.gameExistsByGameName(any())).thenReturn(true);
    assertFalse(gameService.saveGame(game).isPresent());
  }

  @Test
  void ensureDeletingExistingGameWorksAndReturnsDeletedGame() {
    Game game = TestFixtures.game();

    when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.of(game));

    assertEquals(gameService.deleteGame(game.getToken()).get(), game);

  }

  @Test
  void ensureDeletingNotExistingGameReturnsOptionalEmpty() {
    Game game = TestFixtures.game();

    when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.empty());
    assertEquals(gameService.deleteGame(game.getToken()), Optional.empty());
  }

  @Test
  void ensureUpdatingExistingGameWorksAndReturnsUpdatedGame() {
    //Given
    Game gameWithNewValues = TestFixtures.game();

    //When
    when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.of(gameOld));
    when(gameRepository.updateGame(any())).thenReturn(
        Optional.of(gameWithNewValues));

    var updatedGame = gameService.updateGame(gameWithNewValues);

    //Then

    assertEquals(updatedGame.get().getId(), gameWithNewValues.getId());
  }

  @Test
  void ensureUpdatingNotExistingGameReturnsOptionalEmpty() {
    Game gameWithNewValues = TestFixtures.game();

    assertEquals(gameService.updateGame(gameWithNewValues),
        Optional.empty());
  }

  @Test
  void ensureFindGameByIdForExistingGameReturnsGame() {
    Game game = TestFixtures.game();
    when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.of(game));
    assertEquals(gameService.findGameByToken(game.getToken()).get(), game);
  }

  @Test
  void ensureFindGameByIdForNotExistingGameReturnsOptionalEmpty() {
    Game game = TestFixtures.game();
    when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.empty());

    assertEquals(gameService.findGameByToken(game.getToken()), Optional.empty());
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
