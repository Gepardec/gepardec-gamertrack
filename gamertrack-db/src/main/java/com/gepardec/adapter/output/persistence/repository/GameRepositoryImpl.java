package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.GameMapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class GameRepositoryImpl implements GameRepository, Serializable {

  @Inject
  private EntityManager em;

  @Inject
  private GameMapper gameMapper;

  @Override
  public Optional<Game> saveGame(Game game) {
    GameEntity gameEntity = this.gameMapper.gameModelToGameEntity(game);

    em.persist(gameEntity);
    em.flush();
    GameEntity foundGameEntity = em.find(GameEntity.class, gameEntity.getId());
    return Optional.ofNullable(this.gameMapper.gameEntityToGameModel(foundGameEntity));
  }

  @Override
  public void deleteGame(Long gameId) {
    em.remove(em.find(GameEntity.class, gameId));
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    GameEntity gameEntityOld = em.find(GameEntity.class, game.getId());

    return gameEntityOld != null
        ? Optional.of(this.gameMapper.gameEntityToGameModel(
        em.merge(this.gameMapper.gameModelToExitstingGameEntity(game, gameEntityOld))))
        : Optional.empty();
  }

  @Override
  public Optional<Game> findGameByToken(String token) {
    var found = em.createQuery(
            "select g from GameEntity g where g.token = :token",
            GameEntity.class)
        .setParameter("token", token)
        .getResultList().stream().findFirst();

    return found.map(gameEntity -> gameMapper.gameEntityToGameModel(gameEntity));
  }

  @Override
  public List<Game> findAllGames() {
    return em.createQuery("select g from GameEntity g", GameEntity.class)
        .getResultList()
        .stream()
        .map(gameMapper::gameEntityToGameModel)
        .toList();
  }

  @Override
  public Boolean gameExistsByGameName(String gameName) {
    Query query = em.createQuery("select g from GameEntity g where g.name = :gameName",
        GameEntity.class);
    query.setParameter("gameName", gameName);

    return !query.getResultList().isEmpty();
  }

  @Override
  public Boolean existsByGameToken(String gameToken) {
    return findGameByToken(gameToken).isPresent();
  }
}