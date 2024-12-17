package com.gepardec.core.repository;

import com.gepardec.model.Game;
import com.gepardec.model.dto.GameDto;
import java.util.List;
import java.util.Optional;

public interface GameRepository {

  Optional<Game> saveGame(GameDto gameDto);

  void deleteGame(Long gameId);

  Optional<Game> updateGame(GameDto gameDto);

  Optional<Game> findGameById(long id);

  List<Game> findAllGames();

  Boolean GameExistsByGameName(String name);

  Optional<Game> findGameReferenceByGameId(Long gameId);

  Boolean existsByGameId(Long gameId);
}

