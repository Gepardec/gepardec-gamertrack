package com.gepardec.adapter.output.persistence.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
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
    UserEntity user = TestFixtures.user(1L);

    User userDto = new User(user);

    UserEntity mappedUser = entityMapper.toUser(userDto);

    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapUserDtoToExistingUserWorks() {
    UserEntity user = TestFixtures.user(1L);

    User userDto = new User(user);

    UserEntity mappedUser = entityMapper.toExistingUser(userDto, user);

    assertEquals(mappedUser.getId(), user.getId());
    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapScoreDtoToScoreWorks() {
    ScoreEntity score = TestFixtures.score(1L, 1L, 1L);

    Score scoreDto = new Score(score);

    when(entityManager.getReference(UserEntity.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(GameEntity.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    ScoreEntity mappedScore = entityMapper.toScore(scoreDto);

    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }

  @Test
  public void ensureMapScoreDtoToExistingScoreWorks() {
    ScoreEntity score = TestFixtures.score(1L, 1L, 1L);

    Score scoreDto = new Score(score);

    when(entityManager.getReference(UserEntity.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(GameEntity.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    ScoreEntity mappedScore = entityMapper.toExistingScore(scoreDto, score);

    assertEquals(mappedScore.getId(), scoreDto.id());
    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }


  @Test
  void ensureMatchDtoToMatchWithReferenceWorks() {
    Match match = new Match(1L, 1L, List.of(1L));

    when(entityManager.getReference(GameEntity.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(UserEntity.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    MatchEntity mappedMatch = entityMapper.toMatchWithReference(match);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedMatch.getId(), match.id());
    assertEquals(mappedMatch.getGame().getId(), match.gameId());
    assertTrue(mappedMatch.getUsers().stream().map(UserEntity::getId).toList()
        .containsAll(match.userIds()));

  }

  @Test
  void ensureMatchDtoToMatchWithReferenceWorksProvidingDtoAndEntity() {
    Match match = new Match(1L, 1L, List.of(1L));

    when(entityManager.getReference(GameEntity.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(UserEntity.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    MatchEntity mappedGameOutcome = entityMapper.toMatchWithReference(match,
        TestFixtures.match(1L, TestFixtures.game(), List.of(TestFixtures.user(1L))));

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedGameOutcome.getId(), match.id());
    assertEquals(mappedGameOutcome.getGame().getId(), match.gameId());
    assertTrue(mappedGameOutcome.getUsers().stream().map(UserEntity::getId).toList()
        .containsAll(match.userIds()));
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorks() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game());

    GameEntity mappedGame = entityMapper.toGame(game);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.title(), mappedGame.getName());
    assertEquals(game.rules(), mappedGame.getRules());
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorksProvidingDtoAndEntity() {
    Game game = TestFixtures.gameToGameDto(TestFixtures.game());

    GameEntity mappedGame = entityMapper.toGame(game, TestFixtures.game());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.id(), mappedGame.getId());
    assertEquals(game.title(), mappedGame.getName());
    assertEquals(game.rules(), mappedGame.getRules());
  }
}
