package com.gepardec.interfaces.services;

import com.gepardec.model.Game;
import jakarta.data.repository.Query;
import jakarta.persistence.NamedNativeQuery;
import java.util.List;
import java.util.Optional;
import org.jboss.jdeparser.FormatPreferences.Opt;

public interface GameService {
  Optional<Game> saveGame(Game game);
  void deleteGame(Game game);
  Optional<Game> updateGame(Game game);
  Optional<Game> findGameById(long id);
  List<Game> findAll();
}
