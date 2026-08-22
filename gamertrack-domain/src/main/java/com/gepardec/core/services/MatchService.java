package com.gepardec.core.services;

import com.gepardec.model.Match;
import jakarta.data.page.PageRequest;

import java.util.List;
import java.util.Optional;

public interface MatchService {

    /**
     * Saves a match and updates the Elo ratings of all participants in one transaction.
     * <p>
     * Every participant must already have a score for the match's game. If any
     * participant has no score (e.g. because their default scores were removed on
     * deactivation), the match is rejected and nothing is persisted — a match is
     * never saved without its rating updates and vice versa.
     *
     * @return the saved match, or {@link Optional#empty()} if the match is invalid
     * or a participant has no score for the game
     */
    Optional<Match> saveMatch(Match match);

    List<Match> findAllMatches();

    Optional<Match> findMatchByToken(String token);

    Optional<Match> deleteMatch(String matchToken);

    Optional<Match> updateMatch(Match matchDto);

    List<Match> findAllFilteredOrUnfilteredMatches(
            Optional<String> gameToken,
            Optional<String> userToken,
            PageRequest pageRequest);

    long countAllFilteredOrUnfilteredMatches(Optional<String> gameToken, Optional<String> userToken);
}
