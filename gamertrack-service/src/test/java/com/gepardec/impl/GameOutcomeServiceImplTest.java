package com.gepardec.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.adapters.output.persistence.repository.GameOutcomeRepositoryImpl;
import com.gepardec.adapters.output.persistence.repository.GameRepositoryImpl;
import com.gepardec.adapters.output.persistence.repository.UserRepositoryImpl;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameOutcomeServiceImplTest {

  @Mock
  EntityManager entityManager;

  @Mock
  GameOutcomeRepositoryImpl gameOutcomeRepository;

  @Mock
  UserRepositoryImpl userRepository;
  @Mock
  GameRepositoryImpl gameRepository;

  @InjectMocks
  GameOutcomeServiceImpl gameService;


  @Test
  void ensureSaveAndReadGameWorks() {
    GameOutcome gameOutcome = TestFixtures.gameOutcome();

    when(gameOutcomeRepository.saveGameOutcome(any())).thenReturn(Optional.of(gameOutcome));
    when(userRepository.findUserReferencesById(any())).thenReturn(Optional.of(TestFixtures.user()));
    when(gameRepository.findGameReferenceByGameId(any())).thenReturn(Optional.of(TestFixtures.game()));


    assertEquals(gameService.saveGameOutcome(gameOutcome.getGame().getId(), gameOutcome.getUsers().stream().map(
            User::getId).toList()),
        Optional.of(gameOutcome));
  }

}