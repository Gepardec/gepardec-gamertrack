package com.gepardec.impl.service;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.model.Game;
import com.gepardec.model.dtos.GameDto;
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
  public Optional<Game> saveGame(GameDto gameDto) {
    if (gameRepository.GameExistsByGameName(gameDto.title())) {
      return Optional.empty();
    }
    return gameRepository.saveGame(gameDto);
  }

  @Override
  public Optional<Game> deleteGame(Long gameId) {
    Optional<Game> game = gameRepository.findGameById(gameId);
    if (game.isEmpty()) {
      return Optional.empty();
    }

    gameRepository.deleteGame(gameId);

    return game;
  }

  @Override
  public Optional<Game> updateGame(GameDto gameDto) {

    if (gameDto != null) {
      return gameRepository.updateGame(gameDto);
    }

    return Optional.empty();
  }

  @Override
  public Optional<Game> findGameById(long id) {
    return gameRepository.findGameById(id);
  }

  @Override
  public List<Game> findAllGames() {
    return gameRepository.findAllGames();
  }
}

