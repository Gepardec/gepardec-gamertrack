package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.adapter.output.persistence.repository.mapper.EntityMapper;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
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
    EntityMapper entityMapper;
    @Override
    public Optional<Score> saveScore(Score score) {
        if(entityManager.find(GameEntity.class, score.getGame().getId()) != null) {
            if(entityManager.find(UserEntity.class, score.getUser().getId()) != null) {
                ScoreEntity scoreEntity = entityMapper.ScoreModeltoScoreEntity(score);
                entityManager.persist(scoreEntity);

                ScoreEntity scoreSaved = entityManager.find(ScoreEntity.class, scoreEntity.getId());
                log.info("Save score with userId: {}, gameId: {} and {} scorePoints.", scoreSaved.getUser().getId(), scoreSaved.getGame().getId(), scoreSaved.getScorePoints());
                return Optional.of(entityMapper.ScoreEntityToScoreModel(scoreSaved));
            }
            log.error("User with id: {} does not exist!", score.getUser().getId());
            return Optional.empty();
        }
        log.error("Game with id: {} does not exist!", score.getGame().getId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> updateScore(Score score) {
        if(entityManager.find(GameEntity.class, score.getGame().getId()) != null) {
            if(entityManager.find(UserEntity.class, score.getUser().getId()) != null) {
                ScoreEntity scoreEntity = entityMapper.ScoreModeltoExistingScoreEntity(score, entityManager.find(ScoreEntity.class, score.getId()));
                entityManager.merge(scoreEntity);

                ScoreEntity scoreMerged = entityManager.find(ScoreEntity.class, score.getId());
                log.info("Updated score with userId: {}, gameId: {} and {} scorePoints.", scoreMerged.getUser().getId(), scoreMerged.getGame().getId(), scoreMerged.getScorePoints());
                return Optional.of(entityMapper.ScoreEntityToScoreModel(scoreMerged));
            }
            log.error("User with id: {} does not exist! Updating Aborted", score.getUser().getId());
            return Optional.empty();
        }
        log.error("Game with id: {} does not exist! Updating Aborted", score.getGame().getId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        List<ScoreEntity> resultList = entityManager.createQuery(
                "SELECT s FROM ScoreEntity s " +
                        "WHERE s.id = :id ", ScoreEntity.class)
                .setParameter("id", id)
                .getResultList();
        log.info("Find score with id: {}. Returned list of size:{}", id, resultList.size());

        return resultList.isEmpty()
                ? Optional.empty()
                : Optional.of(entityMapper.ScoreEntityToScoreModel(resultList.getFirst()));
    }

    @Override
    public List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId, Boolean includeDeactivatedUsers) {
        List<ScoreEntity> resultList = entityManager.createQuery(
                "SELECT s FROM ScoreEntity s " +
                        "WHERE (:minPoints is null OR :minPoints <= s.scorePoints) " +
                        "AND (:maxPoints is null OR s.scorePoints <= :maxPoints) " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                        "AND (:userId is null OR s.user.id = :userId) " +
                        "AND (:gameId is null OR s.game.id = :gameId) " +
                        "order by s.scorePoints", ScoreEntity.class)
                .setParameter("minPoints", minPoints)
                .setParameter("maxPoints", maxPoints)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .setParameter("userId", userId)
                .setParameter("gameId", gameId)
                .getResultList();
        log.info("Filter score by minPoints: {}, maxPoints: {}, userId: {}, gameId: {}, includeDeactivatedUser{}. Resultsize: {}", minPoints,maxPoints, userId, gameId, includeDeactivatedUsers, resultList.size());

        return resultList.stream().map(entityMapper::ScoreEntityToScoreModel).collect(Collectors.toList());
    }

    @Override
    public List<Score> findTopScoreByGame(Long gameId, int top, Boolean includeDeactivatedUsers) {
        if(top<=0){return List.of();}

        List<ScoreEntity> resultList = entityManager.createQuery(
                "SELECT s FROM ScoreEntity s " +
                        "WHERE s.game.id = :gameId " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) " +
                        "order by s.scorePoints DESC", ScoreEntity.class)
                .setParameter("gameId", gameId)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .setMaxResults(top)
                .getResultList();
        log.info("Find Top {} score with gameId: {}. Returned list of size:{}", top, gameId, resultList.size());
        return resultList.stream().map(entityMapper::ScoreEntityToScoreModel).collect(Collectors.toList());
    }

    @Override
    public List<Score> findScoreByScorePoints(double scorePoints, Boolean includeDeactivatedUsers) {
        List<ScoreEntity> resultList = entityManager.createQuery(
                "SELECT s FROM ScoreEntity s " +
                        "WHERE s.scorePoints = :scorePoints " +
                        "AND (:includeDeactivatedUsers = true OR s.user.deactivated = false) ", ScoreEntity.class)
                .setParameter("scorePoints", scorePoints)
                .setParameter("includeDeactivatedUsers", includeDeactivatedUsers)
                .getResultList();
        log.info("Find score with {} ScorePoints. Returned list of size:{}", scorePoints, resultList.size());

        return resultList.stream().map(entityMapper::ScoreEntityToScoreModel).collect(Collectors.toList());
    }

    @Override
    public boolean scoreExists(Score scoreDto) {
        List<ScoreEntity> entity = entityManager.createQuery(
                        "SELECT s FROM ScoreEntity s " +
                                "WHERE s.game.id = :gameId " +
                                "AND s.user.id =:userId ", ScoreEntity.class)
                .setParameter("gameId", scoreDto.getGame().getId())
                .setParameter("userId", scoreDto.getUser().getId())
                .getResultList();
        log.info("Score with userId: {} and gameId: {} exists: {}", scoreDto.getUser().getId(), scoreDto.getGame().getId(), !entity.isEmpty());

        return !entity.isEmpty();
    }

}
