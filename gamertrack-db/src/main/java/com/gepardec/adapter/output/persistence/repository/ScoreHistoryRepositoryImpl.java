package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.ScoreHistoryEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.ScoreHistoryMapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreHistoryRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.ScoreHistory;
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
public class ScoreHistoryRepositoryImpl implements ScoreHistoryRepository, Serializable {

  private static final Logger log = LoggerFactory.getLogger(ScoreHistoryRepositoryImpl.class);

  @PersistenceContext()
  protected EntityManager entityManager;

  @Inject
  UserRepository userRepository;
  @Inject
  GameRepository gameRepository;

  @Inject
  ScoreHistoryMapper entityMapper;

  @Override
  public Optional<ScoreHistory> saveScoreHistory(ScoreHistory scoreHistory) {

    Optional<Game> dbGame = gameRepository.findGameByToken(scoreHistory.getGame().getToken());
    Optional<User> dbUser = userRepository.findUserByToken(scoreHistory.getUser().getToken());

    if (dbGame.isPresent()) {
      if (dbUser.isPresent()) {
        scoreHistory.setUser(dbUser.get());
        scoreHistory.setGame(dbGame.get());
        ScoreHistoryEntity scoreHistoryEntity = entityMapper.scoreHistoryModelToScoreHistoryEntity(
            scoreHistory);
        entityManager.persist(scoreHistoryEntity);

        ScoreHistoryEntity scoreHistorySaved = entityManager.find(ScoreHistoryEntity.class,
            scoreHistoryEntity.getId());
        log.info(
            "Saved score history with user Token: {}, game Token: {}, match Token: {} and score change: {}",
            scoreHistorySaved.getUser().getToken(), scoreHistorySaved.getGame().getToken(),
            scoreHistorySaved.getMatchToken(), scoreHistorySaved.getScoreChange());
        return Optional.of(entityMapper.scoreHistoryEntityToScoreHistoryModel(scoreHistorySaved));
      }
      log.error("User with Token: {} does not exist!", scoreHistory.getUser().getToken());
      return Optional.empty();
    }
    log.error("Game with Token: {} does not exist!", scoreHistory.getGame().getToken());
    return Optional.empty();
  }

  @Override
  public Optional<ScoreHistory> findScoreHistoryByToken(String token) {
    List<ScoreHistoryEntity> resultList = entityManager.createQuery(
            "SELECT sh FROM ScoreHistoryEntity sh " +
                "WHERE sh.token = :token ", ScoreHistoryEntity.class)
        .setParameter("token", token)
        .getResultList();
    log.info("Find score history with Token: {}. Returned list of size:{}", token,
        resultList.size());

    return resultList.isEmpty()
        ? Optional.empty()
        : Optional.of(entityMapper.scoreHistoryEntityToScoreHistoryModel(resultList.getFirst()));
  }

  @Override
  public List<ScoreHistory> filterScoreHistory(String userToken, String gameToken,
      String matchToken) {
    List<ScoreHistoryEntity> resultList = entityManager.createQuery(
            "SELECT sh FROM ScoreHistoryEntity sh " +
                "WHERE (:userToken is null OR sh.user.token = :userToken) " +
                "AND (:gameToken is null OR sh.game.token = :gameToken) " +
                "AND (:matchToken is null OR sh.matchToken = :matchToken) " +
                "order by sh.createdOn, sh.id", ScoreHistoryEntity.class)
        .setParameter("userToken", userToken)
        .setParameter("gameToken", gameToken)
        .setParameter("matchToken", matchToken)
        .getResultList();
    log.info(
        "Filter score history by user Token: {}, game Token: {}, match Token: {}. Resultsize: {}",
        userToken, gameToken, matchToken, resultList.size());

    return resultList.stream().map(entityMapper::scoreHistoryEntityToScoreHistoryModel)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteScoreHistoryByGame(String gameToken) {
    int deletedCount = entityManager.createQuery(
            "DELETE FROM ScoreHistoryEntity sh " +
                "WHERE sh.game.id in (SELECT g.id FROM GameEntity g WHERE g.token = :gameToken)")
        .setParameter("gameToken", gameToken)
        .executeUpdate();
    log.info("Deleted {} score history entries for game Token: {}", deletedCount, gameToken);
  }
}
