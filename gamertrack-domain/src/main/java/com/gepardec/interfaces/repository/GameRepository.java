package com.gepardec.interfaces.repository;

import com.gepardec.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameRepository {

  void saveGame(Game game);
  void deleteGame(Game game);
  void updateGame(Game game);
  Optional<Game> findGameById(long id);
  List<Game> findAll();

}
