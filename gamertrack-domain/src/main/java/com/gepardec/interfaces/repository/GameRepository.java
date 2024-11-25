package com.gepardec.interfaces.repository;

import com.gepardec.model.Game;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.List;
import java.util.Optional;

public interface GameRepository {

  Optional<Game> saveGame(Game game);
  void deleteGame(Game game);
  Optional<Game> updateGame(Game game);
  Optional<Game> findGameById(long id);
  List<Game> findAll();
  Boolean existsByGameName(Game name);

}
