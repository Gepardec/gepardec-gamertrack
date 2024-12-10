package com.gepardec.impl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.dtos.GameOutcomeDto;
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
  GameOutcomeRepository gameOutcomeRepository;

  @Mock
  UserRepository userRepository;
  @Mock
  GameRepository gameRepository;

  @InjectMocks
  GameOutcomeServiceImpl gameOutcomeService;


  @Test
  void ensureSavingValidGameOutcomeReturnsOptionalGameOutcome() {
    GameOutcomeDto gameOutcomeDto = TestFixtures.gameOutcometoGameOutcomeDto(
        TestFixtures.gameOutcome());

    when(gameOutcomeRepository.saveGameOutcome(any())).thenReturn(
        Optional.of(TestFixtures.gameOutcome()));
    when(gameRepository.existsByGameId(anyLong())).thenReturn(true);
    when(userRepository.existsByUserId(anyList())).thenReturn(true);

    assertEquals(gameOutcomeService.saveGameOutcome(gameOutcomeDto).get().getId(),
        TestFixtures.gameOutcome().getId());
  }

  @Test
  void ensureSavingInvalidGameOutcomeReferencingNotExistingGameReturnsEmptyOptional() {
    //Given
    GameOutcome gameOutcome = TestFixtures.gameOutcome();
    gameOutcome.setUsers(TestFixtures.users(10));

    //When
    var savedGameOutcome = gameOutcomeService.saveGameOutcome(
        TestFixtures.gameOutcometoGameOutcomeDto(gameOutcome));

    //Then
    assertEquals(Optional.empty(), savedGameOutcome);
  }

  @Test
  void ensureSavingInvalidGameOutcomeReferencingNoUsersReturnsEmptyOptional() {
    //Given
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    //When
    var savedGameOutcome = gameOutcomeService.saveGameOutcome(
        TestFixtures.gameOutcometoGameOutcomeDto(gameOutcome));

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
    GameOutcome gameOutcomeNew = TestFixtures.gameOutcome(1L);
    GameOutcomeDto gameOutcomeNewDto = TestFixtures.gameOutcometoGameOutcomeDto(gameOutcomeNew);

    //When

    when(gameOutcomeRepository.updateGameOutcome(any())).thenReturn(Optional.of(gameOutcomeNew));
    when(gameRepository.existsByGameId(anyLong())).thenReturn(true);
    when(userRepository.existsByUserId(anyList())).thenReturn(true);
    when(gameOutcomeRepository.existsGameOutcomeById(anyLong())).thenReturn(true);

    var updatedGameOutcome = gameOutcomeService.updateGameOutcome(gameOutcomeNewDto);

    //Then
    assertEquals(gameOutcomeNew.getId(), updatedGameOutcome.get().getId());
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
