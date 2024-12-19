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

  List<Match> findMatchesByUserId(Long userId);

  List<Match> findMatchesByGameId(Long gameId);

  Boolean existsMatchById(Long gameOutcomeId);

  List<Match> findMatchesByUserIdAndGameId(Long userId, Long gameId);


}