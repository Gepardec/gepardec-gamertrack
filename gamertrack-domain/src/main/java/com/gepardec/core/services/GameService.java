package com.gepardec.core.services;

import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {

  Optional<Game> saveGame(Game game);

  Optional<Game> deleteGame(String gameId);

  Optional<Game> updateGame(Game game);

  Optional<Game> findGameByToken(String token);

  List<Game> findAllGames();
}
