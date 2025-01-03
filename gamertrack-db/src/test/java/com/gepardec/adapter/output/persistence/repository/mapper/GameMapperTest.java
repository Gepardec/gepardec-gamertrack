package com.gepardec.adapter.output.persistence.repository.mapper;

import static com.gepardec.TestFixtures.game;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.model.Game;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameMapperTest {

  @Mock
  EntityManager entityManager;

  @InjectMocks
  GameMapper entityMapper = new GameMapper();


  @Test
  void ensureGameModelToGameWithReferenceEntityMappingWorks() {
    Game game = TestFixtures.gameToGameDto(game());

    GameEntity mappedGame = entityMapper.gameModelToGameEntity(game);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.getToken(), mappedGame.getToken());
    assertEquals(game.getName(), mappedGame.getName());
    assertEquals(game.getRules(), mappedGame.getRules());
  }

  @Test
  void ensureGameModelToGameWithReferenceMappingWorksProvidingModelAndEntity() {
    Game game = TestFixtures.gameToGameDto(game());

    GameEntity existingGameEntity = new GameEntity(game.getId(), game.getToken(), game.getName(),
        game.getRules());

    GameEntity mappedGame = entityMapper.gameModelToExitstingGameEntity(game, existingGameEntity);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.getToken(), mappedGame.getToken());
    assertEquals(game.getId(), mappedGame.getId());
    assertEquals(game.getName(), mappedGame.getName());
    assertEquals(game.getRules(), mappedGame.getRules());
  }

}







