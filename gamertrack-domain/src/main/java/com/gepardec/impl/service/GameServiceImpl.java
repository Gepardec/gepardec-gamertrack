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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Transactional
public class GameServiceImpl implements GameService, Serializable {

  private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);


  @Inject
  private GameRepository gameRepository;
  @Inject
  TokenService tokenService;

  @Override
  public Optional<Game> saveGame(Game game) {
    logger.info("Saving game: %s".formatted(game));

    if (gameRepository.gameExistsByGameName(game.getName())) {
      return Optional.empty();
    }
    game.setToken(tokenService.generateToken());
    return gameRepository.saveGame(game);
  }

  @Override
  public Optional<Game> deleteGame(String token) {
    logger.info("Deleting game with token %s".formatted(token));
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
      logger.error("Game is null");
      return Optional.empty();
    }

    logger.info("Updating game with token %s".formatted(game.getToken()));
    Optional<Game> gameOld = findGameByToken(game.getToken());

    if (gameOld.isPresent()) {
      game.setId(gameOld.get().getId());
      logger.info("Updating game with new values %s".formatted(game));
      newGame = gameRepository.updateGame(game);
    }

    logger.error("Game with token %s could not be found".formatted(game.getToken()));
    return newGame;

  }

  @Override
  public Optional<Game> findGameByToken(String token) {
    logger.info("Finding game with token %s".formatted(token));
    return gameRepository.findGameByToken(token);
  }

  @Override
  public List<Game> findAllGames() {
    logger.info("Finding all games");
    return gameRepository.findAllGames();
  }
}

