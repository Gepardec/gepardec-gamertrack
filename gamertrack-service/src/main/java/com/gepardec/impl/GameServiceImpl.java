package com.gepardec.impl;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class GameServiceImpl implements GameService, Serializable {

  @Inject
  private GameRepository gameRepository;

  @Override
  public Optional<Game> saveGame(Game game) {
    if (gameRepository.existsByGameName(game.getName())) {
  /*    throw new GameAlreadyExistsException(
          "Game with Name %s already exists and considered as duplicate".formatted(game.getName()));*/
      return Optional.empty();
    }
        return gameRepository.saveGame(game);
  }

  @Override
  public void deleteGame(Long gameId) {
    if (gameRepository.findGameById(gameId).isEmpty()) {
      throw new GameDoesNotExistException("Game with id %s does not exist".formatted(gameId));
    }
      gameRepository.deleteGame(gameId);
  }

  @Override
  public Optional<Game> updateGame(Long gameId, Game gameNew) {

    Optional<Game> gameOld = gameRepository.findGameById(gameId);

    if (gameOld.isPresent()) {
      Game game = gameOld.get();
      game.setName(gameNew.getName());
      game.setRules(gameNew.getRules());
      return gameRepository.saveGame(game);
    }

    return Optional.empty();
  }

  @Override
  public Optional<Game> findGameById(long id) {
    return gameRepository.findGameById(id);
  }

  @Override
  public List<Game> findAll() {
    return gameRepository.findAll();
  }
}

