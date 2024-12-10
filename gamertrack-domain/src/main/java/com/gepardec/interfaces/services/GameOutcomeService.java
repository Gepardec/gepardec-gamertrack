package com.gepardec.interfaces.services;

import com.gepardec.model.GameOutcome;
import com.gepardec.model.dtos.GameOutcomeDto;
import java.util.List;
import java.util.Optional;

public interface GameOutcomeService {

  Optional<GameOutcome> saveGameOutcome(GameOutcomeDto gameOutcomeDto);

  List<GameOutcome> findAllGameOutcomes();

  Optional<GameOutcome> findGameOutcomeById(Long id);

  Optional<GameOutcome> deleteGameOutcome(Long gameOutcomeId);

  Optional<GameOutcome> updateGameOutcome(GameOutcomeDto gameOutcomeDto);

  List<GameOutcome> findGameOutcomeByUserId(Long userId);

  List<GameOutcome> findGameOutcomesByGameId(Long gameId);

}
