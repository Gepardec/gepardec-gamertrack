package com.gepardec.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.adapters.output.persistence.repository.GameOutcomeRepositoryImpl;
import com.gepardec.adapters.output.persistence.repository.GameRepositoryImpl;
import com.gepardec.adapters.output.persistence.repository.UserRepositoryImpl;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameOutcomeServiceImplTest {

  @Mock
  GameOutcomeRepositoryImpl gameOutcomeRepository;

  @Mock
  UserRepositoryImpl userRepository;
  @Mock
  GameRepositoryImpl gameRepository;

  @InjectMocks
  GameOutcomeServiceImpl gameOutcomeService;


  @Test
  void ensureSavingValidGameOutcomeReturnsOptionalGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.saveGameOutcome(any())).thenReturn(Optional.of(gameOutcome));
    when(userRepository.findUserReferencesById(any())).thenReturn(Optional.of(TestFixtures.user()));
    when(gameRepository.findGameReferenceByGameId(any())).thenReturn(
        Optional.of(TestFixtures.game()));

    assertEquals(gameOutcomeService.saveGameOutcome(gameOutcome.getGame().getId(),
            gameOutcome.getUsers().stream().map(
                User::getId).toList()),
        Optional.of(gameOutcome));
  }

  @Test
  void ensureSavingInvalidGameOutcomeReferencingNotExistingGameReturnsEmptyOptional() {
    //Given
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    //When
    when(gameRepository.findGameReferenceByGameId(any())).thenReturn(Optional.empty());
    var savedGameOutcome = gameOutcomeService.saveGameOutcome(gameOutcome.getId(),
        TestFixtures.userIds(10));

    //Then
    assertEquals(Optional.empty(), savedGameOutcome);
  }

  @Test
  void ensureSavingInvalidGameOutcomeReferencingNoUsersReturnsEmptyOptional() {
    //Given
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    //When
    var savedGameOutcome = gameOutcomeService.saveGameOutcome(gameOutcome.getGame().getId(),
        new ArrayList<>(1));

    //Then
    assertEquals(Optional.empty(), savedGameOutcome);
  }

  @Test
  void ensureFindAllGameOutcomesReturnsAllGameOutcomes() {
    List<GameOutcome> gameOutcomes = TestFixtures.gameOutcomes(10);

    when(gameOutcomeRepository.findAllGameOutcomes()).thenReturn(gameOutcomes);

    assertEquals(gameOutcomes, gameOutcomeService.findAllGameOutcomes());
    assertEquals(gameOutcomes.size(), gameOutcomeService.findAllGameOutcomes().size());
  }

  @Test
  void ensureFindAllGameOutcomesReturnsForNoGameOutcomesEmptyList() {
    when(gameOutcomeRepository.findAllGameOutcomes()).thenReturn(new ArrayList<>());

    assertEquals(0, gameOutcomeService.findAllGameOutcomes().size());
  }

  @Test
  void ensureFindGameOutcomeByIdReturnsGameOutcomeForExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeById(any())).thenReturn(Optional.of(gameOutcome));

    assertEquals(gameOutcome, gameOutcomeService.findGameOutcomeById(gameOutcome.getId()).get());
  }

  @Test
  void ensureFindGameOutcomeByIdReturnsOptionalEmptyForNonExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeById(any())).thenReturn(Optional.empty());

    assertEquals(Optional.empty(), gameOutcomeService.findGameOutcomeById(gameOutcome.getId()));
  }

  @Test
  void ensureDeleteGameOutcomeReturnsDeletedGameOutcomeForExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeById(any())).thenReturn(Optional.of(gameOutcome));
    var deletedGameOutcome = gameOutcomeService.deleteGameOutcome(gameOutcome.getId());

    assertEquals(gameOutcome, deletedGameOutcome.get());
  }

  @Test
  void ensureDeleteGameOutcomeReturnsOptionalEmptyForNonExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeById(any())).thenReturn(Optional.empty());

    var deletedGameOutcome = gameOutcomeService.deleteGameOutcome(gameOutcome.getId());

    assertEquals(Optional.empty(), deletedGameOutcome);
  }

  @Test
  void ensureUpdateGameOutcomeReturnsUpdatedGameOutcomeForExistingGameOutcome() {
    //Given
    GameOutcome gameOutcomeOld = TestFixtures.gameOutcome();
    GameOutcome gameOutcomeNew = TestFixtures.gameOutcome();

    //When
    when(gameOutcomeRepository.findGameOutcomeById(anyLong())).thenReturn(
        Optional.of(gameOutcomeOld));

    when(userRepository.findUserReferencesById(anyLong()))
        .thenAnswer(param -> gameOutcomeNew.getUsers()
            .stream()
            .filter(u -> param.getArgument(0, Long.class).equals(u.getId()))
            .findFirst());

    when(gameRepository.findGameReferenceByGameId(anyLong())).thenReturn(
        Optional.of(gameOutcomeNew.getGame()));

    when(gameOutcomeRepository.saveGameOutcome(any())).thenReturn(Optional.of(gameOutcomeNew));

    var updatedGameOutcome = gameOutcomeService.updateGameOutcome(
        gameOutcomeNew.getId(),
        gameOutcomeNew.getGame().getId(),
        gameOutcomeNew.getUsers().stream().map(User::getId).toList());

    //Then
    assertNotEquals(gameOutcomeOld, updatedGameOutcome.get());
    assertEquals(gameOutcomeNew, updatedGameOutcome.get());
  }

  @Test
  void ensureFindGameOutcomeByUserIdReturnsListOfGameOutcomesForExistingGameOutcomeWithUserId() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeByUserId(anyLong())).thenReturn(
        List.of(gameOutcome));

    var foundGameOutcomes = gameOutcomeService.findGameOutcomeByUserId(
        gameOutcome.getUsers().getFirst().getId());

    assertTrue(foundGameOutcomes.contains(gameOutcome));
  }

  @Test
  void ensureFindGameOutcomeByUserIdReturnsEmptyListForNonExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();
    when(gameOutcomeRepository.findGameOutcomeByUserId(anyLong())).thenReturn(List.of());

    var foundGameOutcomes = gameOutcomeService.findGameOutcomeByUserId(
        gameOutcome.getUsers().getFirst().getId());

    assertTrue(foundGameOutcomes.isEmpty());
  }


  @Test
  void ensureFindGameOutcomeByGameIdReturnsListOfGameOutcomesForExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeByGameId(anyLong())).thenReturn(List.of(gameOutcome));

    var gameOutcomes = gameOutcomeService.findGameOutcomesByGameId(gameOutcome.getGame().getId());

    assertTrue(gameOutcomes.contains(gameOutcome));
  }

  @Test
  void ensureFindGameOutcomeByGameIdReturnsEmptyListForNonExistingGameOutcome() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.findGameOutcomeByGameId(anyLong())).thenReturn(List.of());

    var foundGameOutcomes = gameOutcomeService.findGameOutcomesByGameId(
        gameOutcome.getGame().getId());

    assertTrue(foundGameOutcomes.isEmpty());

  }
}