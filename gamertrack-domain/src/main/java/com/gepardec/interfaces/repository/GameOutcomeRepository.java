package com.gepardec.interfaces.repository;

import com.gepardec.model.GameOutcome;
import java.util.List;
import java.util.Optional;

public interface GameOutcomeRepository {

  Optional<GameOutcome> saveGameOutcome(GameOutcome gameOutcome);
  List<GameOutcome> findAll();
  Optional<GameOutcome> findById(Long id);
  void deleteGameOutcome(Long gameOutcomeId);
  Optional<GameOutcome> updateGameOutcome(Long gameOutcomeId, GameOutcome gameOutcome);
  List<GameOutcome> findGameOutcomeByUserId(Long userId);
  List<GameOutcome> findGameOutcomeByGameId(Long gameId);

}