package com.gepardec.interfaces.repository;

import com.gepardec.model.GameOutcome;
import com.gepardec.model.dtos.GameOutcomeDto;
import java.util.List;
import java.util.Optional;

public interface GameOutcomeRepository {

  Optional<GameOutcome> saveGameOutcome(GameOutcomeDto gameOutcomeDto);

  List<GameOutcome> findAllGameOutcomes();

  Optional<GameOutcome> findGameOutcomeById(Long id);

  void deleteGameOutcome(Long gameOutcomeId);

  Optional<GameOutcome> updateGameOutcome(GameOutcomeDto gameOutcomeDto);

  List<GameOutcome> findGameOutcomeByUserId(Long userId);

  List<GameOutcome> findGameOutcomeByGameId(Long gameId);

  Boolean existsGameOutcomeById(Long gameOutcomeId);


}