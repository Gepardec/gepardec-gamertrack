package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ScoreRepositoryImpl implements ScoreRepository, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ScoreRepositoryImpl.class);
    @PersistenceContext()
    protected EntityManager entityManager;

    @Inject
    Mapper mapper;
    @Override
    public Optional<Score> saveScore(ScoreDto scoreDto) {
        if(entityManager.find(Game.class, scoreDto.gameId()) != null) {
            if(entityManager.find(User.class, scoreDto.userId()) != null) {
                Score score = mapper.toScore(scoreDto);
                entityManager.persist(score);

                Score scoreSaved = entityManager.find(Score.class, score.getId());
                log.info("Save score with userId: {}, gameId: {} and {} scorePoints.", scoreDto.userId(), scoreDto.gameId(), scoreDto.scorePoints());
                return Optional.of(scoreSaved);
            }
            log.error("User with id: {} does not exist!", scoreDto.userId());
            return Optional.empty();
        }
        log.error("Game with id: {} does not exist!", scoreDto.gameId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> updateScore(ScoreDto scoreDto) {
        if(entityManager.find(Game.class, scoreDto.gameId()) != null) {
            if(entityManager.find(User.class, scoreDto.userId()) != null) {
                Score score = mapper.toExistingScore(scoreDto, entityManager.find(Score.class, scoreDto.id()));
                entityManager.merge(score);

                Score scoreMerged = entityManager.find(Score.class, score.getId());
                log.info("Updated score with userId: {}, gameId: {} and {} scorePoints.", scoreDto.userId(), scoreDto.gameId(), scoreDto.scorePoints());
                return Optional.of(scoreMerged);
            }
            log.error("User with id: {} does not exist!", scoreDto.userId());
            return Optional.empty();
        }
        log.error("Game with id: {} does not exist!", scoreDto.gameId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        List<Score> resultList = entityManager.createQuery(
                "SELECT s FROM Score s " +
                        "WHERE s.id = :id ", Score.class)
                .setParameter("id", id)
                .getResultList();
        log.info("Find score with id: {}. Returned list of size:{}", id, resultList.size());

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }

    @Override
    public List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId, Boolean includeDeactivatedUsers) {
        List<Score> resultList = entityManager.createQuery(
                "SELECT s FROM Score s " +
                        "WHERE (:minPoints is null OR :minPoints <= s.scorePoints) " +
                        "AND (:maxPoints is null OR s.scorePoints <= :maxPoints) " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                        "AND (:userId is null OR s.user.id = :userId) " +
                        "AND (:gameId is null OR s.game.id = :gameId) " +
                        "order by s.scorePoints", Score.class)
                .setParameter("minPoints", minPoints)
                .setParameter("maxPoints", maxPoints)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .setParameter("userId", userId)
                .setParameter("gameId", gameId)
                .getResultList();
        log.info("Filter score by minPoints: {}, maxPoints: {}, userId: {}, gameId: {}, includeDeactivatedUser{}. Resultsize: {}", minPoints,maxPoints, userId, gameId, includeDeactivatedUsers, resultList.size());

        return resultList;    }




    @Override
    public List<Score> findTopScoreByGame(Long gameId, int top, Boolean includeDeactivatedUsers) {
        if(top<=0){return List.of();}

        List<Score> resultList = entityManager.createQuery(
                "SELECT s FROM Score s " +
                        "WHERE s.game.id = :gameId " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                        "order by s.scorePoints DESC", Score.class)
                .setParameter("gameId", gameId)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .setMaxResults(top)
                .getResultList();
        log.info("Find Top {} score with gameId: {}. Returned list of size:{}", top, gameId, resultList.size());
        return resultList;
    }

    @Override
    public List<Score> findScoreByScorePoints(double scorePoints, Boolean includeDeactivatedUsers) {
        List<Score> resultList = entityManager.createQuery(
                "SELECT s FROM Score s " +
                        "WHERE s.scorePoints = :scorePoints " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) ", Score.class)
                .setParameter("scorePoints", scorePoints)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .getResultList();
        log.info("Find score with {} ScorePoints. Returned list of size:{}", scorePoints, resultList.size());

        return resultList;
    }

    @Override
    public boolean scoreExists(ScoreDto scoreDto) {
        List<Score> entity = entityManager.createQuery(
                        "SELECT s FROM Score s " +
                                "WHERE s.game.id = :gameId " +
                                "AND s.user.id =:userId ", Score.class)
                .setParameter("gameId", scoreDto.gameId())
                .setParameter("userId", scoreDto.userId())
                .getResultList();
        log.info("Score with userId: {} and gameId: {} exists: {}", scoreDto.userId(), scoreDto.gameId(), !entity.isEmpty());

        return !entity.isEmpty();
    }

}
