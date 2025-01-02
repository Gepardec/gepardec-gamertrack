package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.services.GameService;
import com.gepardec.core.services.TokenService;
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
  @Inject
  TokenService tokenService;

  @Override
  public Optional<Game> saveGame(Game game) {
    if (gameRepository.gameExistsByGameName(game.getName())) {
      return Optional.empty();
    }
    game.setToken(tokenService.generateToken());
    return gameRepository.saveGame(game);
  }

  @Override
  public Optional<Game> deleteGame(String token) {
    Optional<Game> game = gameRepository.findGameByToken(token);
    if (game.isEmpty()) {
      return Optional.empty();
    }

    gameRepository.deleteGame(game.get().getId());

    return game;
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    Optional<Game> newGame = Optional.empty();

    if (game == null) {
      return Optional.empty();
    }

    Optional<Game> gameOld = findGameByToken(game.getToken());

    if (gameOld.isPresent()) {
      game.setId(gameOld.get().getId());
      newGame = gameRepository.updateGame(game);
    }
    
    return newGame;

  }

  @Override
  public Optional<Game> findGameByToken(String token) {
    return gameRepository.findGameByToken(token);
  }

  @Override
  public List<Game> findAllGames() {
    return gameRepository.findAllGames();
  }
}

