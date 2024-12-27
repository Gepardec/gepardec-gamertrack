package com.gepardec.rest.model.mapper;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestMapperTest {

  @InjectMocks
  RestMapper restMapper;

  @Test
  void ensureCreateMatchCmdToMatchDtoWorks() {
    Match mappedMatch =
        restMapper.createMatchCommandtoMatch(RestTestFixtures.createMatchCommand());

    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.getGame().getId(),
        RestTestFixtures.createMatchCommand().game().getId());
    assertTrue(mappedMatch.getUsers().stream().map(User::getId).toList()
        .containsAll(RestTestFixtures.createMatchCommand().users().stream().map(User::getId).toList()));
  }

  @Test
  void ensureUpdateMatchCmdToMatchDtoWorks() {
    Match mappedMatch = restMapper
        .updateMatchCommandtoMatch(RestTestFixtures.updateMatchCommand().game().getId(),
            RestTestFixtures.updateMatchCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.getGame().getId(),
        RestTestFixtures.updateMatchCommand().game().getId());
    assertTrue(mappedMatch.getUsers().stream().map(User::getId).toList()
        .containsAll(RestTestFixtures.updateMatchCommand().users().stream().map(User::getId).toList()));
  }

  @Test
  void ensureCreateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = restMapper.createGameCommandtoGame(RestTestFixtures.createGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.createGameCommand().title(), mappedGame.getName());
    assertEquals(RestTestFixtures.createGameCommand().rules(), mappedGame.getRules());
  }

  @Test
  void ensureUpdateGameCommandToGameCommandDtoWorks() {
    Game mappedGame = restMapper.updateGameCommandtoGame(RestTestFixtures.game().getId(),
        RestTestFixtures.updateGameCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedGame);
    assertEquals(RestTestFixtures.updateGameCommand().title(), mappedGame.getName());
    assertEquals(RestTestFixtures.updateGameCommand().rules(), mappedGame.getRules());
  }

  @Test
  public void ensureMapCreateScoreCommandToScoreDtoWorks() {
    CreateScoreCommand scoreCommand = RestTestFixtures.createScoreCommand(1L);

    Score mappedScore = restMapper.createScoreCommandtoScore(scoreCommand);

    assertEquals(scoreCommand.user().getId(), mappedScore.getUser().getId());
    assertEquals(scoreCommand.game().getId(), mappedScore.getGame().getId());
    assertEquals(scoreCommand.scorePoints(), mappedScore.getScorePoints());

  }

  @Test
  public void ensureMapCreateUserCommandDtoToUserDtoWorks() {
    CreateUserCommand userCommand = RestTestFixtures.createUserCommand(1L);

    User mappedUser = restMapper.createUserCommandtoUser(userCommand);

    assertEquals(userCommand.firstname(), mappedUser.getFirstname());
    assertEquals(userCommand.lastname(), mappedUser.getLastname());

  }

  @Test
  public void ensureMapUpdateUserCommandDtoToUserDtoWorks() {

    UpdateUserCommand updateUserCommand = RestTestFixtures.updateUserCommand(1L);

    User mappedUser = restMapper.updateUserCommandtoUser(1L, updateUserCommand);

    assertEquals(1L, mappedUser.getId());
    assertEquals(updateUserCommand.firstname(), mappedUser.getFirstname());
    assertEquals(updateUserCommand.lastname(), mappedUser.getLastname());
    assertEquals(updateUserCommand.deactivated(), mappedUser.isDeactivated());

  }
}