package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.EntityMapper;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.model.Match;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class MatchRepositoryImpl implements MatchRepository {

  private final Logger logger = LoggerFactory.getLogger(MatchRepositoryImpl.class);

  @Inject
  private EntityManager em;

  @Inject
  EntityMapper entityMapper;

  @Override
  public Optional<Match> saveMatch(Match matchDto) {
    logger.info("  match {}", matchDto);

    MatchEntity match = entityMapper.matchModelToMatchEntity(matchDto);

    em.persist(match);

    return findMatchById(match.getId());
  }

  @Override
  public List<Match> findAllMatches() {
    logger.info("Finding all matches");
    return em.createQuery("select go from MatchEntity go", MatchEntity.class)
        .getResultList()
        .stream()
        .map(entityMapper::matchEntityToMatchModel)
        .toList();
  }

  @Override
  public Optional<Match> findMatchById(Long id) {
    logger.info("Finding game outcome by id: %s".formatted(id));

    return Optional.ofNullable(em.find(MatchEntity.class, id))
        .map(entityMapper::matchEntityToMatchModel);
  }

  @Override
  public void deleteMatch(Long matchId) {
    logger.info("Looking up matches by id: %s in order to delet".formatted(matchId));
    Optional<Match> matchToDelete = findMatchById(matchId);

    if (matchToDelete.isEmpty()) {
      logger.info(
          "Could not find match with ID %s".formatted(matchId));
    }

    logger.info("Deleting match with id: %s".formatted(matchId));
    matchToDelete.ifPresent(match -> em.remove(match));
  }

  @Override
  public Optional<Match> updateMatch(Match matchNew) {
    logger.info("updating game outcome with id: %s".formatted(matchNew.getId()));

    Optional<Match> match = findMatchById(matchNew.getId());

    return match
        .map(game -> entityMapper.matchModelToMatchEntityWithReference(matchNew,
            entityMapper.matchModelToMatchEntity(game)))
        .map(em::merge)
        .map(entityMapper::matchEntityToMatchModel);

  }

  @Override
  public List<Match> findMatchesByUserId(Long userId) {
    logger.info("Finding all matches by userId: %s".formatted(userId));
    var query = em.createQuery(
        "select go from MatchEntity go inner join go.users u where u.id = :userId ",
        MatchEntity.class);

    query.setParameter("userId", userId);
    return query.getResultList().stream().map(entityMapper::matchEntityToMatchModel).toList();
  }

  @Override
  public List<Match> findMatchesByGameId(Long gameId) {
    logger.info("Finding all games outcomes by gameId: %s".formatted(gameId));
    var query = em.createQuery("select go from MatchEntity go where go.game.id = :gameId ",
        MatchEntity.class);

    query.setParameter("gameId", gameId);
    return query.getResultList().stream().map(entityMapper::matchEntityToMatchModel).toList();
  }

  @Override
  public List<Match> findMatchesByUserIdAndGameId(Long userId, Long gameId) {
    logger.info("Finding matches by UserId: {} and GameId: {}".formatted(userId, gameId));
    var query = em.createQuery(
        "select m from MatchEntity m inner join m.users u where (:userId is NULL OR u.id = :userId) and (:gameId is NULL OR m.game.id = :gameId)",
        MatchEntity.class);
    query.setParameter("userId", userId);
    query.setParameter("gameId", gameId);
    return query.getResultList().stream().map(entityMapper::matchEntityToMatchModel).toList();
  }

  @Override
  public Boolean existsMatchById(Long matchId) {
    return findMatchById(matchId).isPresent();
  }
}
