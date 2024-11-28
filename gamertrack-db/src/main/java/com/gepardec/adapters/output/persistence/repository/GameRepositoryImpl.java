package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GameRepositoryImpl implements GameRepository, Serializable {


  @Inject
  private EntityManager em;

  @Override
  public Optional<Game> saveGame(Game game) {
    em.persist(game);

    return Optional.ofNullable(em.find(Game.class, game.getId()));
  }

  @Override
  public void deleteGame(Long gameId) {
    em.remove(em.find(Game.class, gameId));
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    return saveGame(game);
  }

  @Override
  public Optional<Game> findGameById(long id) {
    return Optional.ofNullable(em.find(Game.class, id));

  }

  @Override
  public List<Game> findAllGames() {
    return em.createQuery("select g from Game g", Game.class).getResultList();
  }

  @Override
  public Boolean GameExistsByGameName(String gameName) {
     Query query = em.createQuery("select g from Game g where g.name = :gameName", Game.class);
     query.setParameter("gameName", gameName);

     return !query.getResultList().isEmpty();
  }
}