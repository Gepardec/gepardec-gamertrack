package com.gepardec.core.services;

import com.gepardec.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchService {

  Optional<Match> saveMatch(Match gameOutcomeDto);

  List<Match> findAllMatches();

  Optional<Match> findMatchById(Long id);

  Optional<Match> deleteMatch(Long gameOutcomeId);

  Optional<Match> updateMatch(Match gameOutcomeDto);

  List<Match> findMatchesByUserId(Long userId);

  List<Match> findMatchesByGameId(Long gameId);

  List<Match> findMatchesByUserIdAndGameId(Optional<Long> userId, Optional<Long> gameId);

}
