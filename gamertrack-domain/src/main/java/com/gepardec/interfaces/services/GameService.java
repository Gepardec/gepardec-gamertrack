package com.gepardec.interfaces.services;

import com.gepardec.model.Game;
import com.gepardec.model.dtos.GameDto;
import java.util.List;
import java.util.Optional;

public interface GameService {

  Optional<Game> saveGame(GameDto gameDto);

  Optional<Game> deleteGame(Long gameId);

  Optional<Game> updateGame(GameDto gameDto);

  Optional<Game> findGameById(long id);

  List<Game> findAllGames();
}
