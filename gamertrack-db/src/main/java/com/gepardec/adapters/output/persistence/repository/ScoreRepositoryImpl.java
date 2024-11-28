package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.ScoreRepository;
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
    public Optional<Score> findById(Long id) {
        List<Score> resultList = entityManager.createQuery("SELECT s FROM Score s WHERE s.id = :id", Score.class)
                .setParameter("id", id)
                .getResultList();

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public List<Score> findByUser(Long userId) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.user.id = :userId", Score.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Score> findByGame(Long gameId) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.game.id = :gameId", Score.class)
                .setParameter("gameId", gameId)
                .getResultList();
    }

    @Override
    public List<Score> findByScorePoints(double scorePoints) {
        return entityManager.createQuery("SELECT s FROM Score s WHERE s.scorePoints = :scorePoints", Score.class)
                .setParameter("scorePoints", scorePoints)
                .getResultList();
    }

    @Override
    public Optional<Score> saveScore(Score score) {
        List<Score> entity = entityManager.createQuery(
                "SELECT s FROM Score s " +
                        "WHERE s.game.id = :gameId and s.user.id =:userId ", Score.class)
                .setParameter("gameId", score.game.getId())
                .setParameter("userId", score.user.getId())
                        .getResultList();
        if(entity.isEmpty()) {
            entityManager.persist(score);
            Score scoreSaved = entityManager.find(Score.class, score.getId());
            return Optional.of(scoreSaved);
        }
        return Optional.empty();
    }
}
