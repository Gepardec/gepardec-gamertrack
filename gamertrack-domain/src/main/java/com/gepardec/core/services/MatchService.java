package com.gepardec.core.services;

import com.gepardec.model.Match;
import com.gepardec.model.dto.MatchDto;
import java.util.List;
import java.util.Optional;

public interface MatchService {

  Optional<Match> saveMatch(MatchDto gameOutcomeDto);

  List<Match> findAllMatches();

  Optional<Match> findMatchById(Long id);

  Optional<Match> deleteMatch(Long gameOutcomeId);

  Optional<Match> updateMatch(MatchDto gameOutcomeDto);

  List<Match> findMatchByUserId(Long userId);

  List<Match> findMatchsByGameId(Long gameId);

  List<Match> findMatchesByUserIdAndGameId(Optional<Long> userId, Optional<Long> gameId);

}
