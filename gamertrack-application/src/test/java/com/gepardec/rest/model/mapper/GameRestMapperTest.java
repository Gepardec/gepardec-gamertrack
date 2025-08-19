package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


public class GameRestMapperTest {

  @InjectMocks
  private GameRestMapper gameRestMapper;

  @Test
  void ensureCreateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = gameRestMapper.createGameCommandtoGame(RestTestFixtures.createGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.createGameCommand().name(), mappedGame.getName());
    assertEquals(RestTestFixtures.createGameCommand().rules(), mappedGame.getRules());
  }

  @Test
  void ensureUpdateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = gameRestMapper.updateGameCommandtoGame(RestTestFixtures.game().getToken(),
        RestTestFixtures.updateGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.updateGameCommand().name(), mappedGame.getName());
    assertEquals(RestTestFixtures.updateGameCommand().rules(), mappedGame.getRules());
  }

}
