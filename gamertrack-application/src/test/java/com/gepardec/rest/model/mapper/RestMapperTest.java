package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
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

  @Test
  public void ensureMapCreateScoreCommandToScoreDtoWorks() {
    CreateScoreCommand scoreCommand = RestTestFixtures.createScoreCommand(1L);

    ScoreDto mappedScoreDto = restMapper.toScore(scoreCommand);

    assertEquals(scoreCommand.userId(), mappedScoreDto.userId());
    assertEquals(scoreCommand.gameId(), mappedScoreDto.gameId());
    assertEquals(scoreCommand.scorePoints(), mappedScoreDto.scorePoints());

  }

  @Test
  public void ensureMapCreateUserCommandDtoToUserDtoWorks() {
    CreateUserCommand userCommand = RestTestFixtures.createUserCommand(1L);

    UserDto mappedUserDto = restMapper.CreateUserCommandtoUser(userCommand);

    assertEquals(userCommand.firstname(), mappedUserDto.firstname());
    assertEquals(userCommand.lastname(), mappedUserDto.lastname());

  }

  @Test
  public void ensureMapUpdateUserCommandDtoToUserDtoWorks() {

    UpdateUserCommand updateUserCommand = RestTestFixtures.updateUserCommand(1L);

    UserDto mappedUserDto = restMapper.UpdateUserCommandtoUser(1L, updateUserCommand);

    assertEquals(1L, mappedUserDto.id());
    assertEquals(updateUserCommand.firstname(), mappedUserDto.firstname());
    assertEquals(updateUserCommand.lastname(), mappedUserDto.lastname());
    assertEquals(updateUserCommand.deactivated(), mappedUserDto.deactivated());

  }
}