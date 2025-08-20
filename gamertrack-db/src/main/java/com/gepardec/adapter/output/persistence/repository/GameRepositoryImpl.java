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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class GameRepositoryImpl implements GameRepository, Serializable {

  Logger logger = LoggerFactory.getLogger(GameRepositoryImpl.class);

  @Inject
  private EntityManager em;

  @Inject
  private GameMapper gameMapper;

  @Override
  public Optional<Game> saveGame(Game game) {
    logger.info("Saving game %s".formatted(game));
    GameEntity gameEntity = gameMapper.gameModelToGameEntity(game);
    em.persist(gameEntity);
    em.flush();
    Optional<GameEntity> foundGameEntity = Optional.ofNullable(
        em.find(GameEntity.class, gameEntity.getId()));

    foundGameEntity.ifPresentOrElse(fge -> logger.info("Saved game %s".formatted(fge)),
        () -> logger.error("Could not save game %s".formatted(game)));

    return Optional.ofNullable(gameMapper.gameEntityToGameModel(foundGameEntity.get()));
  }

  @Override
  public void deleteGame(Long gameId) {
    logger.info("Deleting game with ID: %s".formatted(gameId));
    em.remove(em.find(GameEntity.class, gameId));
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    Optional<GameEntity> gameEntityOld = Optional.ofNullable(
        em.find(GameEntity.class, game.getId()));

    gameEntityOld.ifPresentOrElse(geo -> logger.info("Updating game %s".formatted(geo)),
        () -> logger.info("Could not update game %s, because no entity with id %s".formatted(game,
            game.getId())));

    return gameEntityOld.map(gameEntity -> this.gameMapper.gameEntityToGameModel(
        em.merge(this.gameMapper.gameModelToExitstingGameEntity(game, gameEntity))));
  }

  @Override
  public Optional<Game> findGameByToken(String token) {
    logger.info("Finding game by token: %s".formatted(token));

    Optional<GameEntity> found = em
        .createQuery("select g from GameEntity g where g.token = :token", GameEntity.class)
        .setParameter("token", token)
        .getResultList()
        .stream()
        .findFirst();

    logger.info("Found game: %s".formatted(found));
    return found.map(gameEntity -> gameMapper.gameEntityToGameModel(gameEntity));
  }

  @Override
  public List<Game> findAllGames() {
    logger.info("Finding all games");
    return em
        .createQuery("select g from GameEntity g", GameEntity.class)
        .getResultList()
        .stream()
        .map(gameMapper::gameEntityToGameModel)
        .toList();
  }

  @Override
  public Boolean gameExistsByGameName(String gameName) {
    Query query = em
        .createQuery("select g from GameEntity g where g.name = :gameName", GameEntity.class);
    query.setParameter("gameName", gameName);

    return !query.getResultList().isEmpty();
  }

  @Override
  public Boolean existsByGameToken(String gameToken) {
    return findGameByToken(gameToken).isPresent();
  }
}