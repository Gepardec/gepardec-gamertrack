package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ScoreRepositoryImpl implements ScoreRepository, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ScoreRepositoryImpl.class);
    @PersistenceContext()
    protected EntityManager entityManager;

    @Override
    public List<Score> findAllScores() {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s where s.user.deactivated = false", Score.class)
                .getResultList();
        log.info("Find all scores. Returned list of size:{}", resultList.size());

        return resultList;
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.id = :id " +
                        "AND s.user.deactivated = false ", Score.class)
                .setParameter("id", id)
                .getResultList();
        log.info("Find score with id: {}. Returned list of size:{}", id, resultList.size());

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public List<Score> findScoreByUser(Long userId) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.user.id = :userId " +
                        "AND s.user.deactivated = false", Score.class)
                .setParameter("userId", userId)
                .getResultList();
        log.info("Find score with userId: {}. Returned list of size:{}", userId, resultList.size());

        return resultList;
    }

    @Override
    public List<Score> findScoreByGame(Long gameId) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.game.id = :gameId " +
                        "AND s.user.deactivated = false", Score.class)
                .setParameter("gameId", gameId)
                .getResultList();
        log.info("Find score with gameId: {}. Returned list of size:{}", gameId, resultList.size());

        return resultList;
    }

    @Override
    public List<Score> findScoreByScorePoints(double scorePoints) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.scorePoints = :scorePoints " +
                        "AND s.user.deactivated = false", Score.class)
                .setParameter("scorePoints", scorePoints)
                .getResultList();
        log.info("Find score with {} ScorePoints. Returned list of size:{}", scorePoints, resultList.size());

        return resultList;
    }

    @Override
    public List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE :minPoints <= s.scorePoints " +
                        "AND s.scorePoints <= :maxPoints AND s.user.deactivated = false order by s.scorePoints", Score.class)
                .setParameter("minPoints", minPoints)
                .setParameter("maxPoints", maxPoints)
                .getResultList();
        log.info("Find score with scorePoints between {} and {}. Returned list of size:{}", minPoints,maxPoints, resultList);

        return resultList;
    }

    @Override
    public Optional<Score> saveScore(Long userId, Long gameId, double scorePoints) {

        if(!scoreExists(userId, gameId)) {
            Score score = new Score();
            score.setUser(entityManager.getReference(User.class, userId));
            score.setGame(entityManager.getReference(Game.class, gameId));
            score.setScorePoints(scorePoints);

            entityManager.persist(score);
            Score scoreSaved = entityManager.find(Score.class, score.getId());
            log.info("Save score with userId: {}, gameId: {} and {} scorePoints.", userId, gameId, scorePoints);
            return Optional.of(scoreSaved);
        }
        log.error("Score with userId: {} and gameId: {} already exists!", userId, gameId);

        return Optional.empty();
    }

    @Override
    public boolean scoreExists(Long userId, Long gameId) {
        List<Score> entity = entityManager.createQuery(
                        "SELECT s FROM Score s " +
                                "WHERE s.game.id = :gameId and s.user.id =:userId ", Score.class)
                .setParameter("gameId", gameId)
                .setParameter("userId", userId)
                .getResultList();
        log.info("Score with userId: {} and gameId: {} exists: {}", userId, gameId, !entity.isEmpty());

        return !entity.isEmpty();
    }

}
