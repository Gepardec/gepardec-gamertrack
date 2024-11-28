package com.gepardec.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.gepardec.adapters.output.persistence.repository.GameRepositoryImpl;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Optional;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)

class GameServiceImplTest {

  @Mock
  EntityManager entityManager;

  @Mock
  GameRepositoryImpl gameRepository;

  @InjectMocks
  GameServiceImpl gameService;


  @Test
  void ensureSaveAndReadGameWorks() {
    Game game = new Game("", "TestRules");

    when(gameRepository.saveGame(game)).thenReturn(Optional.of(game));
    when(gameRepository.GameExistsByGameName("")).thenReturn(true);
    assertEquals(gameService.saveGame(game).get().getName(), game.getName());
  }
}