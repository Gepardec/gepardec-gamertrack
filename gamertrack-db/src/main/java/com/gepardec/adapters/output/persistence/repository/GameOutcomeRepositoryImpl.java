package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.model.GameOutcome;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GameOutcomeRepositoryImpl implements GameOutcomeRepository {

  @Inject
  private EntityManager em;

  @Override
  public Optional<GameOutcome> saveGameOutcome(GameOutcome gameOutcome) {
    em.persist(gameOutcome);

    return findGameOutcomeById(gameOutcome.getId());
  }

  @Override
  public List<GameOutcome> findAllGameOutcomes() {
    var list = em.createQuery("select go from GameOutcome go", GameOutcome.class).getResultList();

    for (GameOutcome gameOutcome : list) {
      System.out.println(gameOutcome);
    }

    return list;
  }

  @Override
  public Optional<GameOutcome> findGameOutcomeById(Long id) {
    return Optional.of(em.find(GameOutcome.class, id));
  }

  @Override
  public void deleteGameOutcome(Long gameOutcomeId) {
    Optional<GameOutcome> gameOutcomeToDelete = findGameOutcomeById(gameOutcomeId);

    gameOutcomeToDelete.ifPresent(gameOutcome -> em.remove(gameOutcome));
  }

  @Override
  public Optional<GameOutcome> updateGameOutcome(Long gameOutcomeId, GameOutcome gameOutcome) {
    return saveGameOutcome(gameOutcome);
  }

  @Override
  public List<GameOutcome> findGameOutcomeByUserId(Long userId) {
    var query = em.createQuery("select go from GameOutcome go inner join go.users u where u.id = :userId ", GameOutcome.class);

    query.setParameter("userId", userId);
    return query.getResultList();
  }

  @Override
  public List<GameOutcome> findGameOutcomeByGameId(Long gameId) {
    var query = em.createQuery("select go from GameOutcome go where go.game.id = :gameId ",
        GameOutcome.class);

    query.setParameter("gameId", gameId);
    return query.getResultList();
  }
}
