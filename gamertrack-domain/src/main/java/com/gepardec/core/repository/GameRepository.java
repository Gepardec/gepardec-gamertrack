package com.gepardec.core.repository;

import com.gepardec.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {

  Optional<Game> saveGame(Game game);

  void deleteGame(Long gameId);

  Optional<Game> updateGame(Game game);

  Optional<Game> findGameById(long id);

  List<Game> findAllGames();

  Boolean gameExistsByGameName(String name);

  Boolean existsByGameId(Long gameId);
}

