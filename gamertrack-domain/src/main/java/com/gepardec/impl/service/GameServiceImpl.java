package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.services.GameService;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.TokenService;
import com.gepardec.core.services.UserService;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class GameServiceImpl implements GameService, Serializable {

  private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

  @Inject
  private GameRepository gameRepository;
  @Inject
  TokenService tokenService;
  @Inject
  private UserService userService;
  @Inject
  private ScoreService scoreService;

  @Override
  public Optional<Game> saveGame(Game game) {
    logger.info("Saving game: %s".formatted(game));

    if (gameRepository.gameExistsByGameName(game.getName())) {
      return Optional.empty();
    }
    game.setToken(tokenService.generateToken());
    Optional<Game> savedGame = gameRepository.saveGame(game);

    List<User> userList = userService.findAllUsers(true);
    System.out.println("userList: " + userList);
    if (!userList.isEmpty()) {

      for (User user : userList) {
        System.out.println("Drinnen");

        scoreService.saveScore(new Score(0L, user, savedGame.get(), 1500L, ""));
      }
    }

    return savedGame;
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

