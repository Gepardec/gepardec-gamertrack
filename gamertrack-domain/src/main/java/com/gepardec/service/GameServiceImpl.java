package com.gepardec.service;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import javax.print.DocFlavor.READER;

public class GameServiceImpl implements GameService {

  @Inject
  private final GameRepository gameRepository;

  @Override
  public Optional<Game> saveGame(Game game) {
    if (!gameRepository.existsByGameName(game)) {
      throw new GameAlreadyExistsException(
          "Game with Name %s already exists and considered as duplicate".formatted(game.getName()));
    }
        return gameRepository.saveGame(game);
  }

  @Override
  public void deleteGame(Game game) {
    gameRepository.deleteGame(game);
  }

  @Override
  public Optional<Game> updateGame(Game game) {
    gameRepository.updateGame(game);
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

