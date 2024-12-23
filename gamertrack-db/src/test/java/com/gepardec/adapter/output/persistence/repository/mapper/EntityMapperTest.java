package com.gepardec.adapter.output.persistence.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EntityMapperTest {

  @Mock
  EntityManager entityManager;

  @InjectMocks
  EntityMapper entityMapper = new EntityMapper();

  @Test
  public void ensureMapUserDtoToUserWorks() {
    com.gepardec.adapter.output.persistence.entity.User user = TestFixtures.user(1L);

    User userDto = new User(user);

    com.gepardec.adapter.output.persistence.entity.User mappedUser = entityMapper.toUser(userDto);

    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapUserDtoToExistingUserWorks() {
    com.gepardec.adapter.output.persistence.entity.User user = TestFixtures.user(1L);

    User userDto = new User(user);

    com.gepardec.adapter.output.persistence.entity.User mappedUser = entityMapper.toExistingUser(userDto, user);

    assertEquals(mappedUser.getId(), user.getId());
    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapScoreDtoToScoreWorks() {
    com.gepardec.adapter.output.persistence.entity.Score score = TestFixtures.score(1L, 1L, 1L);

    Score scoreDto = new Score(score);

    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    com.gepardec.adapter.output.persistence.entity.Score mappedScore = entityMapper.toScore(scoreDto);

    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }

  @Test
  public void ensureMapScoreDtoToExistingScoreWorks() {
    com.gepardec.adapter.output.persistence.entity.Score score = TestFixtures.score(1L, 1L, 1L);

    Score scoreDto = new Score(score);

    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    com.gepardec.adapter.output.persistence.entity.Score mappedScore = entityMapper.toExistingScore(scoreDto, score);

    assertEquals(mappedScore.getId(), scoreDto.id());
    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }


  @Test
  void ensureMatchDtoToMatchWithReferenceWorks() {
    Match match = new Match(1L, 1L, List.of(1L));

    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    com.gepardec.adapter.output.persistence.entity.Match mappedMatch = entityMapper.toMatchWithReference(match);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedMatch.getId(), match.id());
    assertEquals(mappedMatch.getGame().getId(), match.gameId());
    assertTrue(mappedMatch.getUsers().stream().map(com.gepardec.adapter.output.persistence.entity.User::getId).toList()
        .containsAll(match.userIds()));

  }

  @Test
  void ensureMatchDtoToMatchWithReferenceWorksProvidingDtoAndEntity() {
    Match match = new Match(1L, 1L, List.of(1L));

    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    com.gepardec.adapter.output.persistence.entity.Match mappedGameOutcome = entityMapper.toMatchWithReference(match,
        TestFixtures.match(1L, TestFixtures.game(), List.of(TestFixtures.user(1L))));

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedGameOutcome.getId(), match.id());
    assertEquals(mappedGameOutcome.getGame().getId(), match.gameId());
    assertTrue(mappedGameOutcome.getUsers().stream().map(com.gepardec.adapter.output.persistence.entity.User::getId).toList()
        .containsAll(match.userIds()));
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorks() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game());

    com.gepardec.adapter.output.persistence.entity.Game mappedGame = entityMapper.toGame(game);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.title(), mappedGame.getName());
    assertEquals(game.rules(), mappedGame.getRules());
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorksProvidingDtoAndEntity() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game());

    com.gepardec.adapter.output.persistence.entity.Game mappedGame = entityMapper.toGame(game, TestFixtures.game());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.id(), mappedGame.getId());
    assertEquals(game.title(), mappedGame.getName());
    assertEquals(game.rules(), mappedGame.getRules());
  }
}
