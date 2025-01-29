package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Score;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ScoreMapper {

  @PersistenceContext
  private EntityManager entityManager;

  @Inject
  private UserMapper userMapper;

  @Inject
  private GameMapper gameMapper;

  public ScoreEntity scoreModeltoScoreEntity(Score score) {
    return new ScoreEntity(entityManager.getReference(UserEntity.class, score.getUser().getId()),
        entityManager.getReference(GameEntity.class, score.getGame().getId()),
        score.getScorePoints(), score.getToken(), score.isDefaultScore());
  }

  public Score scoreEntityToScoreModel(ScoreEntity scoreEntity) {
    return new Score(scoreEntity.getId(), userMapper.userEntityToUserModel(scoreEntity.getUser()),
        gameMapper.gameEntityToGameModel(scoreEntity.getGame()), scoreEntity.getScorePoints(),scoreEntity.getToken(),
            scoreEntity.isDefaultScore());
  }

  public ScoreEntity scoreModeltoExistingScoreEntity(Score score, ScoreEntity scoreEntity) {
    scoreEntity.setUser(entityManager.getReference(UserEntity.class, score.getUser().getId()));
    scoreEntity.setGame(entityManager.getReference(GameEntity.class, score.getGame().getId()));
    scoreEntity.setScorePoints(score.getScorePoints());
    scoreEntity.setDefaultScore(score.isDefaultScore());
    return scoreEntity;
  }
}
