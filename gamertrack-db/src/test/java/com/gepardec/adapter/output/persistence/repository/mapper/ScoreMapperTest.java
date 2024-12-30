package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Score;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScoreMapperTest {

  @Mock
  EntityManager entityManager;

  @InjectMocks
  ScoreMapper scoreMapper = new ScoreMapper();

  @Test
  public void ensureScoreModelToScoreEntityMappingWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);

    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false);
    GameEntity gameEntity = new GameEntity(4L, "4Gewinnt", "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
        userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
        gameEntity);

    ScoreEntity mappedScore = scoreMapper.scoreModeltoScoreEntity(score);

    assertEquals(mappedScore.getUser().getId(), score.getUser().getId());
    assertEquals(mappedScore.getGame().getId(), score.getGame().getId());
    assertEquals(mappedScore.getScorePoints(), score.getScorePoints());
  }

  @Test
  public void ensureScoreModelToExistingScoreEntityWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);
    ScoreEntity existingScore = new ScoreEntity(null, null, 10L, null);
    existingScore.setId(1L);

    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false);
    GameEntity gameEntity = new GameEntity(4L, "4Gewinnt", "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
        userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
        gameEntity);

    ScoreEntity mappedScore = scoreMapper.scoreModeltoExistingScoreEntity(score, existingScore);

    assertEquals(score.getId(), mappedScore.getId());
    assertEquals(score.getUser().getId(), mappedScore.getUser().getId());
    assertEquals(score.getGame().getId(), mappedScore.getGame().getId());
    assertEquals(score.getScorePoints(), mappedScore.getScorePoints());
  }


}
