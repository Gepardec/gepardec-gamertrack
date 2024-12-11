package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.adapters.output.persistence.repository.mapper.Mapper;
import com.gepardec.interfaces.repository.MatchRepository;
import com.gepardec.model.Match;
import com.gepardec.model.dtos.MatchDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MatchRepositoryImpl implements MatchRepository {

  private final Logger logger = LoggerFactory.getLogger(MatchRepositoryImpl.class);

  @Inject
  private EntityManager em;

  @Inject
  Mapper mapper;
  @Inject
  private EntityManager entityManager;

  @Override
  public Optional<Match> saveMatch(MatchDto matchDto) {
    logger.info("  match {}", matchDto);

    Match match = mapper.toMatchWithReference(matchDto);

    em.persist(match);

    return findMatchById(match.getId());
  }

  @Override
  public List<Match> findAllMatches() {
    logger.info("Finding all matches");
    return em.createQuery("select go from Match go", Match.class).getResultList();
  }

  @Override
  public Optional<Match> findMatchById(Long id) {
    logger.info("Finding game outcome by id: %s".formatted(id));
    return Optional.ofNullable(em.find(Match.class, id));
  }

  @Override
  public void deleteMatch(Long matchId) {
    logger.info("Looking up game outcome by id: %s in order to delet".formatted(matchId));
    Optional<Match> matchToDelete = findMatchById(matchId);

    if (matchToDelete.isEmpty()) {
      logger.info(
          "Could not find match with ID %s".formatted(matchId));
    }

    logger.info("Deleting match with id: %s".formatted(matchId));
    matchToDelete.ifPresent(match -> em.remove(match));
  }

  @Override
  public Optional<Match> updateMatch(MatchDto matchDto) {
    logger.info("updating game outcome with id: %s".formatted(matchDto.id()));

    Optional<Match> match = findMatchById(matchDto.id());

    return match
        .map(game -> mapper.toMatchWithReference(matchDto, game))
        .map(entityManager::merge);

  }

  @Override
  public List<Match> findMatchByUserId(Long userId) {
    logger.info("Finding all matches by userId: %s".formatted(userId));
    var query = em.createQuery(
        "select go from Match go inner join go.users u where u.id = :userId ",
        Match.class);

    query.setParameter("userId", userId);
    return query.getResultList();
  }

  @Override
  public List<Match> findMatchByGameId(Long gameId) {
    logger.info("Finding all games outcomes by gameId: %s".formatted(gameId));
    var query = em.createQuery("select go from Match go where go.game.id = :gameId ",
        Match.class);

    query.setParameter("gameId", gameId);
    return query.getResultList();
  }

  @Override
  public Boolean existsMatchById(Long matchId) {
    return findMatchById(matchId).isPresent();
  }
}
