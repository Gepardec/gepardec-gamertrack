package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.adapters.output.persistence.repository.mapper.Mapper;
import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.dtos.GameOutcomeDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GameOutcomeRepositoryImpl implements GameOutcomeRepository {

  private final Logger logger = LoggerFactory.getLogger(GameOutcomeRepositoryImpl.class);

  @Inject
  private EntityManager em;

  @Inject
  Mapper mapper;
  @Inject
  private EntityManager entityManager;

  @Override
  public Optional<GameOutcome> saveGameOutcome(GameOutcomeDto gameOutcomeDto) {
    logger.info("  gameOutcome {}", gameOutcomeDto);

    GameOutcome gameOutcome = mapper.toGameOutcomeWithReference(gameOutcomeDto);

    em.persist(gameOutcome);

    return findGameOutcomeById(gameOutcome.getId());
  }

  @Override
  public List<GameOutcome> findAllGameOutcomes() {
    logger.info("Finding all gameoutcomes");
    return em.createQuery("select go from GameOutcome go", GameOutcome.class).getResultList();
  }

  @Override
  public Optional<GameOutcome> findGameOutcomeById(Long id) {
    logger.info("Finding game outcome by id: %s".formatted(id));
    return Optional.ofNullable(em.find(GameOutcome.class, id));
  }

  @Override
  public void deleteGameOutcome(Long gameOutcomeId) {
    logger.info("Looking up game outcome by id: %s in order to delet".formatted(gameOutcomeId));
    Optional<GameOutcome> gameOutcomeToDelete = findGameOutcomeById(gameOutcomeId);

    if (gameOutcomeToDelete.isEmpty()) {
      logger.info(
          "Could not find gameoutcome with ID %s".formatted(gameOutcomeId));
    }

    logger.info("Deleting gameoutcome with id: %s".formatted(gameOutcomeId));
    gameOutcomeToDelete.ifPresent(gameOutcome -> em.remove(gameOutcome));
  }

  @Override
  public Optional<GameOutcome> updateGameOutcome(GameOutcomeDto gameOutcomeDto) {
    logger.info("updating game outcome with id: %s".formatted(gameOutcomeDto.id()));

    Optional<GameOutcome> gameOutcome = findGameOutcomeById(gameOutcomeDto.id());

    return gameOutcome
        .map(game -> mapper.toGameOutcomeWithReference(gameOutcomeDto, game))
        .map(entityManager::merge);

  }

  @Override
  public List<GameOutcome> findGameOutcomeByUserId(Long userId) {
    logger.info("Finding all games outcomes by userId: %s".formatted(userId));
    var query = em.createQuery(
        "select go from GameOutcome go inner join go.users u where u.id = :userId ",
        GameOutcome.class);

    query.setParameter("userId", userId);
    return query.getResultList();
  }

  @Override
  public List<GameOutcome> findGameOutcomeByGameId(Long gameId) {
    logger.info("Finding all games outcomes by gameId: %s".formatted(gameId));
    var query = em.createQuery("select go from GameOutcome go where go.game.id = :gameId ",
        GameOutcome.class);

    query.setParameter("gameId", gameId);
    return query.getResultList();
  }

  @Override
  public Boolean existsGameOutcomeById(Long gameOutcomeId) {
    return findGameOutcomeById(gameOutcomeId).isPresent();
  }
}
