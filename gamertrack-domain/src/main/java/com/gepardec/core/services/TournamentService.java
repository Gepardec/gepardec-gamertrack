package com.gepardec.core.services;

import com.gepardec.model.Match;
import com.gepardec.model.Tournament;
import com.gepardec.model.TournamentStanding;
import com.gepardec.model.TournamentState;
import com.gepardec.model.User;

import java.util.List;
import java.util.Optional;

public interface TournamentService {

    Optional<Tournament> saveTournament(Tournament tournament);

    List<Tournament> findAllTournaments();

    Optional<Tournament> findTournamentByToken(String token);

    Optional<Tournament> deleteTournament(String tournamentToken);

    Optional<Tournament> updateTournamentState(String tournamentToken, TournamentState newState);

    Optional<Tournament> addParticipant(String tournamentToken, String userToken);

    Optional<Tournament> removeParticipant(String tournamentToken, String userToken);

    Optional<Match> saveTournamentMatch(String tournamentToken, List<User> users);

    Optional<List<Match>> findTournamentMatches(String tournamentToken);

    Optional<List<TournamentStanding>> findStandings(String tournamentToken);
}
