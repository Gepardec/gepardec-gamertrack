package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
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
  void ensureCreateMatchCmdToMatchDtoWorks() {
    Match mappedMatch =
        restMapper.toMatchDto(RestTestFixtures.createMatchCommand());

    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.gameId(),
        RestTestFixtures.createMatchCommand().gameId());
    assertTrue(mappedMatch.userIds()
        .containsAll(RestTestFixtures.createMatchCommand().userIds()));
  }

  @Test
  void ensureUpdateMatchCmdToMatchDtoWorks() {
    Match mappedMatch = restMapper
        .toMatchDto(RestTestFixtures.updateMatchCommand().gameId(),
            RestTestFixtures.updateMatchCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.gameId(),
        RestTestFixtures.updateMatchCommand().gameId());
    assertTrue(mappedMatch.userIds()
        .containsAll(RestTestFixtures.updateMatchCommand().userIds()));
  }

  @Test
  void ensureCreateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = restMapper.toGameDto(RestTestFixtures.createGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.createGameCommand().title(), mappedGame.title());
    assertEquals(RestTestFixtures.createGameCommand().rules(), mappedGame.rules());
  }

  @Test
  void ensureUpdateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = restMapper.toGameDto(RestTestFixtures.game().getId(),
        RestTestFixtures.updateGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.updateGameCommand().title(), mappedGame.title());
    assertEquals(RestTestFixtures.updateGameCommand().rules(), mappedGame.rules());
  }

  @Test
  public void ensureMapCreateScoreCommandToScoreDtoWorks() {
    CreateScoreCommand scoreCommand = RestTestFixtures.createScoreCommand(1L);

    Score mappedScore = restMapper.toScore(scoreCommand);

    assertEquals(scoreCommand.userId(), mappedScore.userId());
    assertEquals(scoreCommand.gameId(), mappedScore.gameId());
    assertEquals(scoreCommand.scorePoints(), mappedScore.scorePoints());

  }

  @Test
  public void ensureMapCreateUserCommandDtoToUserDtoWorks() {
    CreateUserCommand userCommand = RestTestFixtures.createUserCommand(1L);

    User mappedUser = restMapper.CreateUserCommandtoUser(userCommand);

    assertEquals(userCommand.firstname(), mappedUser.firstname());
    assertEquals(userCommand.lastname(), mappedUser.lastname());

  }

  @Test
  public void ensureMapUpdateUserCommandDtoToUserDtoWorks() {

    UpdateUserCommand updateUserCommand = RestTestFixtures.updateUserCommand(1L);

    User mappedUser = restMapper.UpdateUserCommandtoUser(1L, updateUserCommand);

    assertEquals(1L, mappedUser.id());
    assertEquals(updateUserCommand.firstname(), mappedUser.firstname());
    assertEquals(updateUserCommand.lastname(), mappedUser.lastname());
    assertEquals(updateUserCommand.deactivated(), mappedUser.deactivated());

  }
}