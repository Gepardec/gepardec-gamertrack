package com.gepardec.interfaces.services;

import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {
  Optional<Game> saveGame(Game game);
  Optional<Game> deleteGame(Long gameId);
  Optional<Game> updateGame(Long gameId, Game game);
  Optional<Game> findGameById(long id);
  List<Game> findAllGames();
}
