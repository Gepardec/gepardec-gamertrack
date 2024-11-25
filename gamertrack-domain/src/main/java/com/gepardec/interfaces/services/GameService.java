package com.gepardec.interfaces.services;

import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameService {

  void saveGame(Game game);
  void deleteGame(Game game);
  void updateGame(Game game);
  Optional<Game> findGameById(long id);
  List<Game> findAll();
}
