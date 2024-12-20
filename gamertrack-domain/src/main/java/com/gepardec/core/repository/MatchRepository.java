package com.gepardec.core.repository;

import com.gepardec.model.Match;
import com.gepardec.model.dto.MatchDto;
import java.util.List;
import java.util.Optional;

public interface MatchRepository {

  Optional<Match> saveMatch(MatchDto matchDto);

  List<Match> findAllMatches();

  Optional<Match> findMatchById(Long id);

  void deleteMatch(Long matchId);

  Optional<Match> updateMatch(MatchDto matchDto);

  List<Match> findMatchByUserId(Long userId);

  List<Match> findMatchByGameId(Long gameId);

  Boolean existsMatchById(Long matchId);


}