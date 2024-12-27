package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gepardec.TestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntityMapperTest {

  @Mock
  EntityManager entityManager;

  @InjectMocks
  EntityMapper entityMapper = new EntityMapper();

  @Test
  public void ensureUserModelToUserEntityMappingWorks() {
    User user = user(1L);

    UserEntity mappedUser = entityMapper.userModelToUserEntity(user);

    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureUserModelToExistingUserEntityMappingWorks() {
    User user = user(1L);

    UserEntity existingUser = new UserEntity(3,"firstname", "lastname",false);

    UserEntity mappedUser = entityMapper.userModeltoExistingUserEntity(user, existingUser);

    assertEquals(existingUser.getId(), mappedUser.getId());
    assertEquals(existingUser.getFirstname(), mappedUser.getFirstname());
    assertEquals(existingUser.getLastname(), mappedUser.getLastname());
    assertEquals(existingUser.isDeactivated(), mappedUser.isDeactivated());
  }

  @Test
  public void ensureScoreModelToScoreEntityMappingWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);

    UserEntity userEntity = new UserEntity(3,"firstname", "lastname",false);
    GameEntity gameEntity = new GameEntity(4L,"4Gewinnt", "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
            userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
            gameEntity);

    ScoreEntity mappedScore = entityMapper.scoreModeltoScoreEntity(score);

    assertEquals(mappedScore.getUser().getId(), score.getUser().getId());
    assertEquals(mappedScore.getGame().getId(), score.getGame().getId());
    assertEquals(mappedScore.getScorePoints(), score.getScorePoints());
  }

  @Test
  public void ensureScoreModelToExistingScoreEntityWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);
    ScoreEntity existingScore = new ScoreEntity(null,null,10L);
    existingScore.setId(1L);

    UserEntity userEntity = new UserEntity(3,"firstname", "lastname",false);
    GameEntity gameEntity = new GameEntity(4L,"4Gewinnt", "Nicht Schummeln");


    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
            userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
            gameEntity);

    ScoreEntity mappedScore = entityMapper.scoreModeltoExistingScoreEntity(score, existingScore);

    assertEquals(score.getId(), mappedScore.getId());
    assertEquals(score.getUser().getId(), mappedScore.getUser().getId());
    assertEquals(score.getGame().getId(), mappedScore.getGame().getId());
    assertEquals(score.getScorePoints(), mappedScore.getScorePoints());
  }


  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorks() {
    Match match = new Match(1L,game(), users(3));

    MatchEntity mappedMatch = entityMapper.matchModelToMatchEntity(match);

    assertDoesNotThrow(() -> NullPointerException.class);
    //assertEquals(match.getId(), mappedMatch.getId());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    assertTrue(match.getUsers().stream().map(User::getId).toList()
            .containsAll(mappedMatch.getUsers().stream().map(UserEntity::getId).toList()));
  }

  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorksProvidingModelAndEntity() {
    Match match = new Match(1L, game(), users(3));
    MatchEntity matchEntity = new MatchEntity();
    matchEntity.setId(1L);

    MatchEntity mappedMatch = entityMapper.matchModelToMatchEntityWithReference(match, matchEntity);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(match.getId(), mappedMatch.getId());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    //assertEquals(4, match.getUsers().size());
    //assertEquals(match.getUsers().size(), mappedMatch.getUsers().size());
    assertTrue(match.getUsers().stream().map(User::getId).toList()
            .containsAll(mappedMatch.getUsers().stream().map(UserEntity::getId).toList()));
  }

  @Test
  void ensureGameModelToGameWithReferenceEntityMappingWorks() {
    Game game = TestFixtures.gameToGameDto(game());

    GameEntity mappedGame = entityMapper.gameModelToGameEntity(game);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.getName(), mappedGame.getName());
    assertEquals(game.getRules(), mappedGame.getRules());
  }

  @Test
  void ensureGameModelToGameWithReferenceMappingWorksProvidingModelAndEntity() {
    Game game = TestFixtures.gameToGameDto(game());

    GameEntity existingGameEntity = new GameEntity(1L,"4Gewinnt", "Nicht Schummeln");

    GameEntity mappedGame = entityMapper.gameModelToExitstingGameEntity(game, existingGameEntity);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(game.getId(), mappedGame.getId());
    assertEquals(game.getName(), mappedGame.getName());
    assertEquals(game.getRules(), mappedGame.getRules());
  }

}







