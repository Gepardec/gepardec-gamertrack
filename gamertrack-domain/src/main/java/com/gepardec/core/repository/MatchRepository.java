package com.gepardec.core.repository;

import com.gepardec.model.Match;
import com.gepardec.model.dto.MatchDto;
import java.util.List;
import java.util.Optional;

public interface MatchRepository {

  Optional<Match> saveMatch(MatchDto gameOutcomeDto);

  List<Match> findAllMatches();

  Optional<Match> findMatchById(Long id);

  void deleteMatch(Long gameOutcomeId);

  Optional<Match> updateMatch(MatchDto gameOutcomeDto);

  List<Match> findMatchByUserId(Long userId);

  List<Match> findMatchByGameId(Long gameId);

  Boolean existsMatchById(Long gameOutcomeId);

  List<Match> findMatchByUserIdAndGameId(Long userId, Long gameId);


}