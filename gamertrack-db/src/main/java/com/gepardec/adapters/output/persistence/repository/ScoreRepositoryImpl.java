package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ScoreRepositoryImpl implements ScoreRepository, Serializable {

    @PersistenceContext()
    protected EntityManager entityManager;

    @Override
    public List<Score> findAllScores() {
        return entityManager.createQuery("SELECT s FROM Score s", Score.class)
                .getResultList();
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.id = :id", Score.class)
                .setParameter("id", id)
                .getResultList();

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public List<Score> findScoreByUser(Long userId) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.user.id = :userId", Score.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Score> findScoreByGame(Long gameId) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.game.id = :gameId", Score.class)
                .setParameter("gameId", gameId)
                .getResultList();
    }

    @Override
    public List<Score> findScoreByScorePoints(double scorePoints) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.scorePoints = :scorePoints", Score.class)
                .setParameter("scorePoints", scorePoints)
                .getResultList();
    }

    @Override
    public List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE :minPoints <= s.scorePoints AND " +
                        "s.scorePoints <= :maxPoints order by s.scorePoints", Score.class)
                .setParameter("minPoints", minPoints)
                .setParameter("maxPoints", maxPoints)
                .getResultList();
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
            return Optional.of(scoreSaved);
        }
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
        return !entity.isEmpty();
    }

}
