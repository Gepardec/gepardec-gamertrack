package com.gepardec.interfaces.services;

import com.gepardec.model.GameOutcome;
import java.util.List;
import java.util.Optional;

public interface GameOutcomeService {

  Optional<GameOutcome> saveGameOutcome(Long gameId, List<Long> userIds);

  List<GameOutcome> findAllGameOutcomes();

  Optional<GameOutcome> findGameOutcomeById(Long id);

  Optional<GameOutcome> deleteGameOutcome(Long gameOutcomeId);

  Optional<GameOutcome> updateGameOutcome(Long gameOutcomeId, Long gameId, List<Long> userIds);

  List<GameOutcome> findGameOutcomeByUserId(Long userId);

  List<GameOutcome> findGameOutcomesByGameId(Long gameId);

}
