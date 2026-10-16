package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.TournamentRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.MatchService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Match;
import com.gepardec.model.Tournament;
import com.gepardec.model.TournamentStanding;
import com.gepardec.model.TournamentState;
import com.gepardec.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gepardec.TestFixtures.tournament;
import static com.gepardec.TestFixtures.user;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {

    @Mock
    TournamentRepository tournamentRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GameRepository gameRepository;

    @Mock
    TokenService tokenService;

    @Mock
    MatchService matchService;

    @InjectMocks
    TournamentServiceImpl tournamentService;

    private void mockExistingGameAndUsers(Tournament tournament) {
        lenient().when(gameRepository.findGameByToken(anyString()))
                .thenReturn(Optional.of(tournament.getGame()));
        lenient().when(userRepository.findUserByToken(anyString())).thenAnswer(
                invocation -> tournament.getParticipants().stream()
                        .filter(user -> user.getToken().equals(invocation.getArgument(0)))
                        .findFirst());
    }

    @Test
    void ensureSavingValidTournamentReturnsOptionalTournament() {
        Tournament tournament = tournament();
        mockExistingGameAndUsers(tournament);

        when(tokenService.generateToken()).thenReturn(tournament.getToken());
        when(tournamentRepository.saveTournament(any())).thenReturn(Optional.of(tournament));

        var savedTournament = tournamentService.saveTournament(tournament);

        assertTrue(savedTournament.isPresent());
        assertEquals(tournament.getId(), savedTournament.get().getId());
    }

    @Test
    void ensureSavingTournamentWithoutNameReturnsEmptyOptional() {
        Tournament tournament = tournament();
        tournament.setName("   ");

        assertEquals(Optional.empty(), tournamentService.saveTournament(tournament));
        verify(tournamentRepository, never()).saveTournament(any());
    }

    @Test
    void ensureSavingTournamentWithTooFewParticipantsReturnsEmptyOptional() {
        Tournament tournament = tournament();
        tournament.setParticipants(List.of(user(1L)));

        assertEquals(Optional.empty(), tournamentService.saveTournament(tournament));
        verify(tournamentRepository, never()).saveTournament(any());
    }

    @Test
    void ensureSavingTournamentWithDuplicateParticipantReturnsEmptyOptional() {
        Tournament tournament = tournament();
        User duplicatedUser = user(1L);
        tournament.setParticipants(List.of(duplicatedUser, duplicatedUser));

        assertEquals(Optional.empty(), tournamentService.saveTournament(tournament));
        verify(tournamentRepository, never()).saveTournament(any());
    }

    @Test
    void ensureSavingTournamentReferencingNotExistingGameReturnsEmptyOptional() {
        Tournament tournament = tournament();

        when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.empty());
        lenient().when(userRepository.findUserByToken(anyString())).thenAnswer(
                invocation -> tournament.getParticipants().stream()
                        .filter(user -> user.getToken().equals(invocation.getArgument(0)))
                        .findFirst());

        assertEquals(Optional.empty(), tournamentService.saveTournament(tournament));
        verify(tournamentRepository, never()).saveTournament(any());
    }

    @Test
    void ensureSavingTournamentReferencingNotExistingUserReturnsEmptyOptional() {
        Tournament tournament = tournament();

        lenient().when(gameRepository.findGameByToken(anyString()))
                .thenReturn(Optional.of(tournament.getGame()));
        when(userRepository.findUserByToken(anyString())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), tournamentService.saveTournament(tournament));
        verify(tournamentRepository, never()).saveTournament(any());
    }

    @Test
    void ensureUpdateTournamentStateForwardWorks() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(tournamentRepository.updateTournament(any()))
                .thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        var updatedTournament =
                tournamentService.updateTournamentState(tournament.getToken(), TournamentState.RUNNING);

        assertTrue(updatedTournament.isPresent());
        assertEquals(TournamentState.RUNNING, updatedTournament.get().getState());
    }

    @Test
    void ensureUpdateTournamentStateBackwardsReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.updateTournamentState(tournament.getToken(), TournamentState.CREATED));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureUpdateTournamentStateFromDoneToRunningReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.DONE);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.updateTournamentState(tournament.getToken(), TournamentState.RUNNING));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureUpdateTournamentStateForNotExistingTournamentReturnsEmptyOptional() {
        when(tournamentRepository.findTournamentByToken(anyString())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(),
                tournamentService.updateTournamentState("notExisting", TournamentState.RUNNING));
    }

    @Test
    void ensureAddParticipantToCreatedTournamentWorks() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);
        User newUser = user(100L);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(userRepository.findUserByToken(newUser.getToken())).thenReturn(Optional.of(newUser));
        when(tournamentRepository.updateTournament(any()))
                .thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        var updatedTournament =
                tournamentService.addParticipant(tournament.getToken(), newUser.getToken());

        assertTrue(updatedTournament.isPresent());
        assertTrue(updatedTournament.get().getParticipants().contains(newUser));
    }

    @Test
    void ensureAddParticipantTwiceReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);
        User existingParticipant = tournament.getParticipants().getFirst();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(userRepository.findUserByToken(existingParticipant.getToken()))
                .thenReturn(Optional.of(existingParticipant));

        assertEquals(Optional.empty(),
                tournamentService.addParticipant(tournament.getToken(), existingParticipant.getToken()));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureAddParticipantToRunningTournamentReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        User newUser = user(100L);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(userRepository.findUserByToken(newUser.getToken())).thenReturn(Optional.of(newUser));

        assertEquals(Optional.empty(),
                tournamentService.addParticipant(tournament.getToken(), newUser.getToken()));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureRemoveParticipantFromCreatedTournamentWorks() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);
        User participantToRemove = tournament.getParticipants().getFirst();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(tournamentRepository.updateTournament(any()))
                .thenAnswer(invocation -> Optional.of(invocation.getArgument(0)));

        var updatedTournament =
                tournamentService.removeParticipant(tournament.getToken(), participantToRemove.getToken());

        assertTrue(updatedTournament.isPresent());
        assertTrue(updatedTournament.get().getParticipants().stream()
                .noneMatch(user -> user.getToken().equals(participantToRemove.getToken())));
    }

    @Test
    void ensureRemoveParticipantFromRunningTournamentReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        User participantToRemove = tournament.getParticipants().getFirst();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.removeParticipant(tournament.getToken(), participantToRemove.getToken()));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureRemoveParticipantBelowMinimumReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);
        tournament.setParticipants(tournament.getParticipants().stream().limit(2).toList());
        User participantToRemove = tournament.getParticipants().getFirst();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.removeParticipant(tournament.getToken(), participantToRemove.getToken()));
        verify(tournamentRepository, never()).updateTournament(any());
    }

    @Test
    void ensureSaveTournamentMatchInRunningTournamentSavesAndLinksMatch() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        List<User> matchUsers = tournament.getParticipants().stream().limit(2).toList();
        Match savedMatch = new Match(42L, "matchToken", tournament.getGame(), matchUsers);

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(matchService.saveMatch(any())).thenReturn(Optional.of(savedMatch));

        var savedTournamentMatch =
                tournamentService.saveTournamentMatch(tournament.getToken(), matchUsers);

        assertTrue(savedTournamentMatch.isPresent());
        assertEquals(savedMatch.getId(), savedTournamentMatch.get().getId());
        verify(tournamentRepository).linkMatchToTournament(tournament.getId(), savedMatch.getId());
    }

    @Test
    void ensureSaveTournamentMatchInCreatedTournamentReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.CREATED);
        List<User> matchUsers = tournament.getParticipants().stream().limit(2).toList();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.saveTournamentMatch(tournament.getToken(), matchUsers));
        verify(matchService, never()).saveMatch(any());
    }

    @Test
    void ensureSaveTournamentMatchWithNonParticipantReturnsEmptyOptional() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        List<User> matchUsers =
                List.of(tournament.getParticipants().getFirst(), user(100L));

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        assertEquals(Optional.empty(),
                tournamentService.saveTournamentMatch(tournament.getToken(), matchUsers));
        verify(matchService, never()).saveMatch(any());
    }

    @Test
    void ensureFindStandingsOrdersByWinsAndListsAllParticipants() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        List<User> participants = tournament.getParticipants();
        User first = participants.get(0);
        User second = participants.get(1);

        List<Match> matches = new ArrayList<>();
        matches.add(new Match(1L, "m1", tournament.getGame(), List.of(first, second)));
        matches.add(new Match(2L, "m2", tournament.getGame(), List.of(first, participants.get(2))));
        matches.add(new Match(3L, "m3", tournament.getGame(), List.of(second, first)));

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(tournamentRepository.findMatchesByTournamentToken(tournament.getToken()))
                .thenReturn(matches);

        var standings = tournamentService.findStandings(tournament.getToken());

        assertTrue(standings.isPresent());
        assertEquals(participants.size(), standings.get().size());

        TournamentStanding leader = standings.get().getFirst();
        assertEquals(first.getToken(), leader.user().getToken());
        assertEquals(2, leader.wins());
        assertEquals(3, leader.matchesPlayed());

        TournamentStanding runnerUp = standings.get().get(1);
        assertEquals(second.getToken(), runnerUp.user().getToken());
        assertEquals(1, runnerUp.wins());
    }

    @Test
    void ensureFindStandingsForNotExistingTournamentReturnsEmptyOptional() {
        when(tournamentRepository.findTournamentByToken(anyString())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), tournamentService.findStandings("notExisting"));
    }

    @Test
    void ensureDeleteTournamentForExistingTournamentReturnsDeletedTournament() {
        Tournament tournament = tournament();

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));

        var deletedTournament = tournamentService.deleteTournament(tournament.getToken());

        assertTrue(deletedTournament.isPresent());
        verify(tournamentRepository).deleteTournament(tournament.getId());
    }

    @Test
    void ensureDeleteTournamentForNotExistingTournamentReturnsEmptyOptional() {
        when(tournamentRepository.findTournamentByToken(anyString())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), tournamentService.deleteTournament("notExisting"));
        verify(tournamentRepository, never()).deleteTournament(any());
    }

    @Test
    void ensureFindTournamentMatchesReturnsMatchesOfTournament() {
        Tournament tournament = tournament(1L, TournamentState.RUNNING);
        List<User> matchUsers = tournament.getParticipants().stream().limit(2).toList();
        List<Match> matches = List.of(new Match(1L, "m1", tournament.getGame(), matchUsers));

        when(tournamentRepository.findTournamentByToken(tournament.getToken()))
                .thenReturn(Optional.of(tournament));
        when(tournamentRepository.findMatchesByTournamentToken(tournament.getToken()))
                .thenReturn(matches);

        var foundMatches = tournamentService.findTournamentMatches(tournament.getToken());

        assertTrue(foundMatches.isPresent());
        assertEquals(matches, foundMatches.get());
    }
}
