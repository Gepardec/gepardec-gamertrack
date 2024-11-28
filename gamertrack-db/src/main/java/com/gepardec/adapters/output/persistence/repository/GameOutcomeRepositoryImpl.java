package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.model.GameOutcome;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class GameOutcomeRepositoryImpl implements GameOutcomeRepository {

  @Inject
  private EntityManager em;

  @Override
  public Optional<GameOutcome> saveGameOutcome(GameOutcome gameOutcome) {
    em.persist(gameOutcome);

    return findById(gameOutcome.getId());
  }

  @Override
  public List<GameOutcome> findAll() {
    return em.createQuery("select go from GameOutcome go", GameOutcome.class).getResultList();
  }

  @Override
  public Optional<GameOutcome> findById(Long id) {
    return Optional.of(em.find(GameOutcome.class, id));
  }

  @Override
  public void deleteGameOutcome(Long gameOutcomeId) {
    em.remove(findGameOutcomeByUserId(gameOutcomeId));
  }

  @Override
  public Optional<GameOutcome> updateGameOutcome(Long gameOutcomeId, GameOutcome gameOutcome) {
    return saveGameOutcome(gameOutcome);
  }

  @Override
  public List<GameOutcome> findGameOutcomeByUserId(Long userId) {
    return em.createQuery("select go from GameOutcome go join go.users u where u.id = :userId ", GameOutcome.class).getResultList();
  }

  @Override
  public List<GameOutcome> findGameOutcomeByGameId(Long gameId) {
    return em.createQuery("select go from GameOutcome go where go.game.id = :gameId ", GameOutcome.class).getResultList();
  }
}
