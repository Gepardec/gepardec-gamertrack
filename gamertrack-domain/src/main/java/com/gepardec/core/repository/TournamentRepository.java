package com.gepardec.core.repository;

import com.gepardec.model.Match;
import com.gepardec.model.Tournament;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository {

    Optional<Tournament> saveTournament(Tournament tournament);

    List<Tournament> findAllTournaments();

    Optional<Tournament> findTournamentById(Long id);

    Optional<Tournament> findTournamentByToken(String token);

    Optional<Tournament> updateTournament(Tournament tournament);

    void deleteTournament(Long tournamentId);

    List<Match> findMatchesByTournamentToken(String tournamentToken);

    void linkMatchToTournament(Long tournamentId, Long matchId);
}
