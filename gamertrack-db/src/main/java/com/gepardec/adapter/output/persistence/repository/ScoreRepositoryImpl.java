package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.ScoreMapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ScoreRepositoryImpl implements ScoreRepository, Serializable {

  private static final Logger log = LoggerFactory.getLogger(ScoreRepositoryImpl.class);
  @PersistenceContext()
  protected EntityManager entityManager;

  @Inject
  UserRepository userRepository;
  @Inject
  GameRepository gameRepository;

  @Inject
  ScoreMapper entityMapper;
    @Inject
    private ScoreMapper scoreMapper;

  @Override
  public Optional<Score> saveScore(Score score) {

    Optional<Game> dbGame = gameRepository.findGameByToken(score.getGame().getToken());
    Optional<User> dbUser = userRepository.findUserByToken(score.getUser().getToken());

    if (dbGame.isPresent()) {
      if (dbUser.isPresent()) {
        score.setUser(dbUser.get());
        score.setGame(dbGame.get());
        ScoreEntity scoreEntity = entityMapper.scoreModeltoScoreEntity(score);
        entityManager.persist(scoreEntity);

        ScoreEntity scoreSaved = entityManager.find(ScoreEntity.class, scoreEntity.getId());
        log.info("Save score with user Token: {}, game Token: {} and {} scorePoints.",
            scoreSaved.getUser().getToken(), scoreSaved.getGame().getToken(),
            scoreSaved.getScorePoints());
        return Optional.of(entityMapper.scoreEntityToScoreModel(scoreSaved));
      }
      log.error("User with Token: {} does not exist!", score.getUser().getToken());
      return Optional.empty();
    }
    log.error("Game with Token: {} does not exist!", score.getGame().getToken());
    return Optional.empty();
  }

  @Override
  public Optional<Score> updateScore(Score score) {
    if (entityManager.find(GameEntity.class, score.getGame().getId()) != null) {
      if (entityManager.find(UserEntity.class, score.getUser().getId()) != null) {
        ScoreEntity scoreEntity = entityMapper.scoreModeltoExistingScoreEntity(score,
            entityManager.find(ScoreEntity.class, score.getId()));
        entityManager.merge(scoreEntity);

        ScoreEntity scoreMerged = entityManager.find(ScoreEntity.class, score.getId());
        log.info("Updated score with user Token: {}, game Token: {} and {} scorePoints.",
            scoreMerged.getUser().getToken(), scoreMerged.getGame().getToken(),
            scoreMerged.getScorePoints());
        return Optional.of(entityMapper.scoreEntityToScoreModel(scoreMerged));
      }
      log.error("User with Token: {} does not exist! Updating Aborted", score.getUser().getToken());
      return Optional.empty();
    }
    log.error("Game with Token: {} does not exist! Updating Aborted", score.getGame().getToken());
    return Optional.empty();
  }

  @Override
  public void deleteScore(Score score) {
    ScoreEntity scoreEntity = scoreMapper.scoreModeltoExistingScoreEntity(score,
            entityManager.find(ScoreEntity.class, findScoreByToken(score.getToken()).get().getId()));
    log.info("Deleted score with token: {}", score.getToken());
    entityManager.remove(scoreEntity);
  }

  @Override
  public Optional<Score> findScoreByToken(String token) {
    List<ScoreEntity> resultList = entityManager.createQuery(
            "SELECT s FROM ScoreEntity s " +
                "WHERE s.token = :token ", ScoreEntity.class)
        .setParameter("token", token)
        .getResultList();
    log.info("Find score with Token: {}. Returned list of size:{}", token, resultList.size());

    return resultList.isEmpty()
        ? Optional.empty()
        : Optional.of(entityMapper.scoreEntityToScoreModel(resultList.getFirst()));
  }

  @Override
  public List<Score> filterScores(Double minPoints, Double maxPoints, String userToken, String gameToken,
      Boolean includeDeactivatedUsers) {
    List<ScoreEntity> resultList = entityManager.createQuery(
            "SELECT s FROM ScoreEntity s " +
                "WHERE (:minPoints is null OR :minPoints <= s.scorePoints) " +
                "AND (:maxPoints is null OR s.scorePoints <= :maxPoints) " +
                "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                "AND (:userToken is null OR s.user.token = :userToken) " +
                "AND (:gameToken is null OR s.game.token = :gameToken) " +
                "order by s.scorePoints", ScoreEntity.class)
        .setParameter("minPoints", minPoints)
        .setParameter("maxPoints", maxPoints)
        .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
        .setParameter("userToken", userToken)
        .setParameter("gameToken", gameToken)
        .getResultList();
    log.info(
        "Filter score by minPoints: {}, maxPoints: {}, user Token: {}, game Token: {}, includeDeactivatedUser{}. Resultsize: {}",
        minPoints, maxPoints, userToken, gameToken, includeDeactivatedUsers, resultList.size());

    return resultList.stream().map(entityMapper::scoreEntityToScoreModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Score> findTopScoreByGame(String gameToken, int top, Boolean includeDeactivatedUsers) {
    if (top <= 0) {
      return List.of();
    }

    List<ScoreEntity> resultList = entityManager.createQuery(
            "SELECT s FROM ScoreEntity s " +
                "WHERE s.game.token = :gameToken " +
                "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                "order by s.scorePoints DESC", ScoreEntity.class)
        .setParameter("gameToken", gameToken)
        .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
        .setMaxResults(top)
        .getResultList();
    log.info("Find Top {} score with game Token: {}. Returned list of size:{}", top, gameToken,
        resultList.size());
    return resultList.stream().map(entityMapper::scoreEntityToScoreModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Score> findScoreByScorePoints(double scorePoints, Boolean includeDeactivatedUsers) {
    List<ScoreEntity> resultList = entityManager.createQuery(
            "SELECT s FROM ScoreEntity s " +
                "WHERE s.scorePoints = :scorePoints " +
                "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) ",
            ScoreEntity.class)
        .setParameter("scorePoints", scorePoints)
        .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
        .getResultList();
    log.info("Find score with {} ScorePoints. Returned list of size:{}", scorePoints,
        resultList.size());

    return resultList.stream().map(entityMapper::scoreEntityToScoreModel)
        .collect(Collectors.toList());
  }

  @Override
  public boolean scoreExists(Score scoreDto) {
    List<ScoreEntity> entity = entityManager.createQuery(
            "SELECT s FROM ScoreEntity s " +
                "WHERE s.game.token = :gameToken " +
                "AND s.user.token =:userToken ", ScoreEntity.class)
        .setParameter("gameToken", scoreDto.getGame().getToken())
        .setParameter("userToken", scoreDto.getUser().getToken())
        .getResultList();
    log.info("Score with userToken: {} and gameToken: {} exists: {}", scoreDto.getUser().getToken(),
        scoreDto.getGame().getToken(), !entity.isEmpty());

    return !entity.isEmpty();
  }

}
