package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.data.repository.Query;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@SessionScoped
public class GameRepositoryImpl implements GameRepository, Serializable {

  @PersistenceContext
  @Produces
  private final EntityManager em;

  @Override
  public Optional<Game> saveGame(Game game) {
    em.persist(game);

    return Optional.ofNullable(em.find(Game.class, game.getId()));
  }

  @Override
  public void deleteGame(Game game) {
    em.remove(em.find(Game.class, game.getId()));
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    return Optional.ofNullable(em.merge(game));
  }

  @Override
  public Optional<Game> findGameById(long id) {
    return Optional.ofNullable(em.find(Game.class, id));

  }

  @Override
  public List<Game> findAll() {
    return em.createQuery("select g from Game ", Game.class).getResultList();
  }

  @Override
  public Boolean existsByGameName(Game name) {
    return !em.createQuery("select s from Game s where s.name = :name", Game.class).getResultList()
        .isEmpty();
  }
}
