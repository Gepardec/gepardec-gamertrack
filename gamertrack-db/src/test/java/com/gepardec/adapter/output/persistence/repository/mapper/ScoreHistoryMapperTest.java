package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreHistoryEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.ScoreHistory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScoreHistoryMapperTest {

  @Mock
  EntityManager entityManager;

  @Spy
  UserMapper userMapper;

  @Spy
  GameMapper gameMapper;

  @InjectMocks
  ScoreHistoryMapper scoreHistoryMapper = new ScoreHistoryMapper();

  @Test
  public void ensureScoreHistoryModelToScoreHistoryEntityMappingWorks() {
    ScoreHistory scoreHistory = TestFixtures.scoreHistory(1L, 3L, 4L);

    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false,
        scoreHistory.getUser().getToken());
    GameEntity gameEntity = new GameEntity(4L, scoreHistory.getGame().getToken(), "4Gewinnt",
        "Nicht Schummeln");

    when(entityManager.getReference(UserEntity.class, scoreHistory.getUser().getId())).thenReturn(
        userEntity);
    when(entityManager.getReference(GameEntity.class, scoreHistory.getGame().getId())).thenReturn(
        gameEntity);

    ScoreHistoryEntity mappedScoreHistory = scoreHistoryMapper.scoreHistoryModelToScoreHistoryEntity(
        scoreHistory);

    assertEquals(scoreHistory.getUser().getToken(), mappedScoreHistory.getUser().getToken());
    assertEquals(scoreHistory.getGame().getToken(), mappedScoreHistory.getGame().getToken());
    assertEquals(scoreHistory.getMatchToken(), mappedScoreHistory.getMatchToken());
    assertEquals(scoreHistory.getPreviousScorePoints(),
        mappedScoreHistory.getPreviousScorePoints());
    assertEquals(scoreHistory.getNewScorePoints(), mappedScoreHistory.getNewScorePoints());
    assertEquals(scoreHistory.getScoreChange(), mappedScoreHistory.getScoreChange());
    assertEquals(scoreHistory.getToken(), mappedScoreHistory.getToken());
  }

  @Test
  public void ensureScoreHistoryEntityToScoreHistoryModelMappingWorks() {
    UserEntity userEntity = new UserEntity(3, "firstname", "lastname", false, "userToken");
    GameEntity gameEntity = new GameEntity(4L, "gameToken", "4Gewinnt", "Nicht Schummeln");

    ScoreHistoryEntity scoreHistoryEntity = new ScoreHistoryEntity(userEntity, gameEntity,
        "matchToken", 1500, 1516, 16, "scoreHistoryToken");
    scoreHistoryEntity.setId(1L);
    scoreHistoryEntity.setCreatedOn(LocalDateTime.of(2026, 1, 1, 12, 0, 0));

    ScoreHistory mappedScoreHistory = scoreHistoryMapper.scoreHistoryEntityToScoreHistoryModel(
        scoreHistoryEntity);

    assertEquals(scoreHistoryEntity.getId(), mappedScoreHistory.getId());
    assertEquals(scoreHistoryEntity.getUser().getToken(),
        mappedScoreHistory.getUser().getToken());
    assertEquals(scoreHistoryEntity.getGame().getToken(),
        mappedScoreHistory.getGame().getToken());
    assertEquals(scoreHistoryEntity.getMatchToken(), mappedScoreHistory.getMatchToken());
    assertEquals(scoreHistoryEntity.getPreviousScorePoints(),
        mappedScoreHistory.getPreviousScorePoints());
    assertEquals(scoreHistoryEntity.getNewScorePoints(), mappedScoreHistory.getNewScorePoints());
    assertEquals(scoreHistoryEntity.getScoreChange(), mappedScoreHistory.getScoreChange());
    assertEquals(scoreHistoryEntity.getToken(), mappedScoreHistory.getToken());
    assertEquals(scoreHistoryEntity.getCreatedOn(), mappedScoreHistory.getCreatedOn());
  }
}
