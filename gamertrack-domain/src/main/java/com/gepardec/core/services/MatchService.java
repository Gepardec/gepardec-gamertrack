package com.gepardec.core.services;

import com.gepardec.model.Match;
import java.util.List;
import java.util.Optional;

public interface MatchService {

  Optional<Match> saveMatch(Match match);

  List<Match> findAllMatches();

  Optional<Match> findMatchByToken(String token);

  Optional<Match> deleteMatch(Long matchId);

  Optional<Match> updateMatch(Match matchDto);

  List<Match> findMatchesByGameTokenAndUserToken(Optional<String> gameToken,
      Optional<String> userToken);

}
