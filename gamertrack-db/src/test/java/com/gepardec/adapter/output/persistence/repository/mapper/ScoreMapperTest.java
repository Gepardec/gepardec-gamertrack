package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.impl.service.TokenServiceImpl;
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
  @InjectMocks
  TokenServiceImpl tokenService;

  @Test
  public void ensureScoreModelToScoreEntityMappingWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);

    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false, score.getUser().getToken());
    GameEntity gameEntity = new GameEntity(4L, score.getGame().getToken(), "4Gewinnt", "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
        userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
        gameEntity);

    ScoreEntity mappedScore = scoreMapper.scoreModeltoScoreEntity(score);

    assertEquals(mappedScore.getUser().getToken(), score.getUser().getToken());
    assertEquals(mappedScore.getGame().getToken(), score.getGame().getToken());
    assertEquals(mappedScore.getScorePoints(), score.getScorePoints());
  }

  @Test
  public void ensureScoreModelToExistingScoreEntityWorks() {
    Score score = TestFixtures.score(1L, 3L, 4L);
    ScoreEntity existingScore = new ScoreEntity(null, null, 10L, null);
    existingScore.setId(1L);
    existingScore.setToken(score.getToken());

    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false, score.getUser().getToken());
    GameEntity gameEntity = new GameEntity(4L, score.getGame().getToken(), "4Gewinnt", "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, score.getUser().getId())).thenReturn(
        userEntity);
    when(entityManager.getReference(GameEntity.class, score.getGame().getId())).thenReturn(
        gameEntity);

    ScoreEntity mappedScore = scoreMapper.scoreModeltoExistingScoreEntity(score, existingScore);

    assertEquals(score.getToken(), mappedScore.getToken());
    assertEquals(score.getUser().getToken(), mappedScore.getUser().getToken());
    assertEquals(score.getGame().getToken(), mappedScore.getGame().getToken());
    assertEquals(score.getScorePoints(), mappedScore.getScorePoints());
  }


}
