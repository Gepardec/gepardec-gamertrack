package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreHistoryEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.ScoreHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ScoreHistoryMapper {

  @PersistenceContext
  private EntityManager entityManager;

  @Inject
  private UserMapper userMapper;

  @Inject
  private GameMapper gameMapper;

  public ScoreHistoryEntity scoreHistoryModelToScoreHistoryEntity(ScoreHistory scoreHistory) {
    return new ScoreHistoryEntity(
        entityManager.getReference(UserEntity.class, scoreHistory.getUser().getId()),
        entityManager.getReference(GameEntity.class, scoreHistory.getGame().getId()),
        scoreHistory.getMatchToken(), scoreHistory.getPreviousScorePoints(),
        scoreHistory.getNewScorePoints(), scoreHistory.getScoreChange(), scoreHistory.getToken());
  }

  public ScoreHistory scoreHistoryEntityToScoreHistoryModel(ScoreHistoryEntity scoreHistoryEntity) {
    return new ScoreHistory(scoreHistoryEntity.getId(), scoreHistoryEntity.getToken(),
        userMapper.userEntityToUserModel(scoreHistoryEntity.getUser()),
        gameMapper.gameEntityToGameModel(scoreHistoryEntity.getGame()),
        scoreHistoryEntity.getMatchToken(), scoreHistoryEntity.getPreviousScorePoints(),
        scoreHistoryEntity.getNewScorePoints(), scoreHistoryEntity.getScoreChange(),
        scoreHistoryEntity.getCreatedOn());
  }
}
