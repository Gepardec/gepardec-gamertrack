package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.model.Game;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class GameMapper {

  @PersistenceContext()
  protected EntityManager entityManager;

  public GameEntity gameModelToGameEntity(Game game) {
    return game.getId() != null
        ? new GameEntity(game.getId(), game.getName(), game.getRules())
        : new GameEntity(null, game.getName(), game.getRules());
  }

  public Game gameEntityToGameModel(GameEntity gameEntity) {
    return gameEntity.getId() != null
        ? new Game(gameEntity.getId(), gameEntity.getName(), gameEntity.getRules())
        : new Game(null, gameEntity.getName(), gameEntity.getRules());
  }

  public GameEntity gameModelToExitstingGameEntity(Game game, GameEntity gameEntity) {
    gameEntity.setRules(game.getRules());
    gameEntity.setName(game.getName());
    return gameEntity;
  }

}
