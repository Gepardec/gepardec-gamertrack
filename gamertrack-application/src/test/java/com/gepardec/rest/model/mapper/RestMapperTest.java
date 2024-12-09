package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestMapperTest {

  @InjectMocks
  RestMapper restMapper;

  @Test
  void ensureCreateGameOutcomeCmdToGameOutcomeDtoWorks() {
    GameOutcomeDto mappedGameOutcomeDto =
        restMapper.toGameOutcomeDto(RestTestFixtures.createGameOutcomeCommand());

    assertNotNull(mappedGameOutcomeDto);
    assertEquals(mappedGameOutcomeDto.gameId(),
        RestTestFixtures.createGameOutcomeCommand().gameId());
    assertTrue(mappedGameOutcomeDto.userIds()
        .containsAll(RestTestFixtures.createGameOutcomeCommand().userIds()));
    assertEquals(mappedGameOutcomeDto.gameId(),
        RestTestFixtures.createGameOutcomeCommand().gameId());
  }

  @Test
  void ensureUpdateGameOutcomeCmdToGameOutcomeDtoWorks() {
    GameOutcomeDto mappedGameOutcomeDto = restMapper
        .toGameOutcomeDto(RestTestFixtures.updateGameOutcomeCommand().gameId(),
            RestTestFixtures.updateGameOutcomeCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGameOutcomeDto);
    assertEquals(mappedGameOutcomeDto.gameId(),
        RestTestFixtures.updateGameOutcomeCommand().gameId());
    assertTrue(mappedGameOutcomeDto.userIds()
        .containsAll(RestTestFixtures.updateGameOutcomeCommand().userIds()));
  }

  @Test
  void ensureCreateGameCommandToGameCommandDtoWorks() {
    GameDto mappedGameDto = restMapper.toGameDto(RestTestFixtures.createGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGameDto);
    assertEquals(RestTestFixtures.createGameCommand().title(), mappedGameDto.title());
    assertEquals(RestTestFixtures.createGameCommand().rules(), mappedGameDto.rules());
  }

  @Test
  void ensureUpdateGameCommandToGameCommandDtoWorks() {
    GameDto mappedGameDto = restMapper.toGameDto(RestTestFixtures.game().getId(),
        RestTestFixtures.updateGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGameDto);
    assertEquals(RestTestFixtures.updateGameCommand().title(), mappedGameDto.title());
    assertEquals(RestTestFixtures.updateGameCommand().rules(), mappedGameDto.rules());
  }
}