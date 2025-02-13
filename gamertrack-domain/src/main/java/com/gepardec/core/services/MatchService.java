package com.gepardec.core.services;

import com.gepardec.model.Match;
import jakarta.data.page.PageRequest;

import java.util.List;
import java.util.Optional;

public interface MatchService {

    Optional<Match> saveMatch(Match match);

    List<Match> findAllMatches(PageRequest pageRequest);

    List<Match> findAllMatches();

    Optional<Match> findMatchByToken(String token);

    Optional<Match> deleteMatch(String matchToken);

    Optional<Match> updateMatch(Match matchDto);

    List<Match> findMatchesByGameTokenAndUserToken(
            Optional<String> gameToken,
            Optional<String> userToken,
            PageRequest pageRequest);
}
