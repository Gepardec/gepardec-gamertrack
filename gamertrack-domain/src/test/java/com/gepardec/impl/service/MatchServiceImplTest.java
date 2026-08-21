package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.EloService;
import com.gepardec.core.services.ScoreHistoryService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.ScoreHistory;
import com.gepardec.model.User;
import jakarta.data.page.PageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gepardec.TestFixtures.match;
import static com.gepardec.TestFixtures.matches;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    MatchRepository matchRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    GameRepository gameRepository;
    @Mock
    TokenService tokenService;

    @InjectMocks
    MatchServiceImpl matchService;
    @Mock
    ScoreServiceImpl scoreService;
    @Mock
    ScoreRepository scoreRepository;
    @Mock
    EloService eloService;
    @Mock
    ScoreHistoryService scoreHistoryService;


    @Test
    void ensureSavingValidMatchReturnsOptionalMatch() {
        Match match = match();

        when(tokenService.generateToken()).thenReturn(match.getToken());

        when(matchRepository.saveMatch(any())).thenReturn(
                Optional.of(match()));
        when(gameRepository.findGameByToken(anyString())).thenReturn(
                Optional.of(match.getGame()));
        when(userRepository.findUserByToken(anyString())).thenAnswer(
                invocation -> match.getUsers().stream()
                        .filter(user -> user.getToken().equals(invocation.getArgument(0)))
                        .findFirst());


        assertEquals(matchService.saveMatch(match).get().getId(),
                match().getId());
    }

    @Test
    void ensureSavingValidMatchPersistsOneScoreHistoryEntryPerParticipant() {
        //Given
        Game game = TestFixtures.game();
        List<User> users = List.of(TestFixtures.user(1L), TestFixtures.user(2L));
        Match match = TestFixtures.match(1L, game, users);

        when(tokenService.generateToken()).thenReturn(match.getToken());
        when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.of(game));
        when(userRepository.findUserByToken(anyString())).thenAnswer(
                invocation -> users.stream()
                        .filter(user -> user.getToken().equals(invocation.getArgument(0)))
                        .findFirst());
        when(matchRepository.saveMatch(any())).thenReturn(Optional.of(match));

        Score oldScoreUser1 = new Score(1L, users.get(0), game, 1500, "scoreToken1", false);
        Score oldScoreUser2 = new Score(2L, users.get(1), game, 1500, "scoreToken2", false);
        when(scoreService.filterScores(null, null, users.get(0).getToken(), game.getToken(), true))
                .thenReturn(List.of(oldScoreUser1));
        when(scoreService.filterScores(null, null, users.get(1).getToken(), game.getToken(), true))
                .thenReturn(List.of(oldScoreUser2));

        Score newScoreUser1 = new Score(1L, users.get(0), game, 1516, "scoreToken1", false);
        Score newScoreUser2 = new Score(2L, users.get(1), game, 1484, "scoreToken2", false);
        when(eloService.updateElo(any(), any(), any()))
                .thenReturn(List.of(newScoreUser1, newScoreUser2));

        //When
        var savedMatch = matchService.saveMatch(match);

        //Then
        assertTrue(savedMatch.isPresent());

        ArgumentCaptor<ScoreHistory> scoreHistoryCaptor = ArgumentCaptor.forClass(
                ScoreHistory.class);
        verify(scoreHistoryService, times(2)).saveScoreHistory(scoreHistoryCaptor.capture());

        List<ScoreHistory> savedScoreHistories = scoreHistoryCaptor.getAllValues();

        ScoreHistory scoreHistoryUser1 = savedScoreHistories.get(0);
        assertEquals(users.get(0).getToken(), scoreHistoryUser1.getUser().getToken());
        assertEquals(game.getToken(), scoreHistoryUser1.getGame().getToken());
        assertEquals(match.getToken(), scoreHistoryUser1.getMatchToken());
        assertEquals(1500, scoreHistoryUser1.getPreviousScorePoints());
        assertEquals(1516, scoreHistoryUser1.getNewScorePoints());
        assertEquals(16, scoreHistoryUser1.getScoreChange());

        ScoreHistory scoreHistoryUser2 = savedScoreHistories.get(1);
        assertEquals(users.get(1).getToken(), scoreHistoryUser2.getUser().getToken());
        assertEquals(game.getToken(), scoreHistoryUser2.getGame().getToken());
        assertEquals(match.getToken(), scoreHistoryUser2.getMatchToken());
        assertEquals(1500, scoreHistoryUser2.getPreviousScorePoints());
        assertEquals(1484, scoreHistoryUser2.getNewScorePoints());
        assertEquals(-16, scoreHistoryUser2.getScoreChange());
    }

    @Test
    void ensureSavingInvalidMatchPersistsNoScoreHistoryEntry() {
        //Given
        Match match = match();
        match.setUsers(TestFixtures.users(10));

        //When
        var savedMatch = matchService.saveMatch(match);

        //Then
        assertEquals(Optional.empty(), savedMatch);
        verify(scoreHistoryService, never()).saveScoreHistory(any());
    }

    @Test
    void ensureSavingInvalidMatchReferencingNotExistingGameReturnsEmptyOptional() {
        //Given
        Match match = match();
        match.setUsers(TestFixtures.users(10));

        //When
        var savedMatch = matchService.saveMatch(match);

        //Then
        assertEquals(Optional.empty(), savedMatch);
    }

    @Test
    void ensureSavingInvalidMatchReferencingNoUsersReturnsEmptyOptional() {
        //Given
        Match match = match();

        //When
        var savedMatch = matchService.saveMatch(match);

        //Then
        assertEquals(Optional.empty(), savedMatch);
    }

    @Test
    void ensureFindAllMatchesReturnsAllMatches() {
        List<Match> matches = matches(10);

        when(matchRepository.findAllMatches()).thenReturn(matches);

        assertEquals(matches, matchService.findAllMatches());
        assertEquals(matches.size(), matchService.findAllMatches().size());
    }

    @Test
    void ensureFindAllMatchesReturnsForNoMatchesEmptyList() {
        when(matchRepository.findAllMatches()).thenReturn(new ArrayList<>());

        assertEquals(0, matchService.findAllMatches().size());
    }

    @Test
    void ensureFindMatchByTokenReturnsMatchForExistingMatch() {
        Match match = match();

        when(matchRepository.findMatchByToken(any())).thenReturn(Optional.of(match));

        assertEquals(match, matchService.findMatchByToken(match.getToken()).get());
    }

    @Test
    void ensureFindMatchByTokenReturnsOptionalEmptyForNonExistingMatch() {
        Match match = match();

        when(matchRepository.findMatchByToken(anyString())).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), matchService.findMatchByToken(match.getToken()));
    }

    @Test
    void ensureDeleteMatchReturnsDeletedMatchForExistingMatch() {
        Match match = match();

        when(matchRepository.findMatchByToken(any())).thenReturn(Optional.of(match));
        var deletedMatch = matchService.deleteMatch(match.getToken());

        assertEquals(match, deletedMatch.get());
    }

    @Test
    void ensureDeleteMatchReturnsOptionalEmptyForNonExistingMatch() {
        Match match = match();

        when(matchRepository.findMatchByToken(any())).thenReturn(Optional.empty());

        var deletedMatch = matchService.deleteMatch(match.getToken());

        assertEquals(Optional.empty(), deletedMatch);
    }

    @Test
    void ensureUpdateMatchReturnsUpdatedMatchForExistingMatch() {
        //Given
        Match matchNew = match(1L);

        //When
        when(gameRepository.findGameByToken(anyString())).thenReturn(Optional.of(matchNew.getGame()));
        when(userRepository.findUserByToken(anyString())).thenAnswer(
                invocation -> matchNew.getUsers().stream()
                        .filter(user -> user.getToken().equals(invocation.getArgument(0))).findFirst());
        when(matchRepository.updateMatch(any())).thenReturn(Optional.of(matchNew));
        when(matchRepository.findMatchByToken(anyString())).thenReturn(Optional.of(matchNew));

        var updatedMatch = matchService.updateMatch(matchNew);

        //Then
        assertEquals(matchNew.getId(), updatedMatch.get().getId());
    }

    @Test
    void ensureFindAllFilteredOrUnfilteredMatchesListOfMatchesForExistingMatch() {
        List<Match> matches = TestFixtures.matches(5);
        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(anyString(), anyString(), any())).thenReturn(
                matches);

        var foundMatches = matchService.findAllFilteredOrUnfilteredMatches(Optional.of(""),
                Optional.of(""), PageRequest.ofPage(10));
        assertTrue(matches.contains(matches.getFirst()));
        assertEquals(matches.size(), foundMatches.size());
    }

    @Test
    void ensureFindAllFilteredOrUnfilteredMatchesReturnsEmptyListForNonExistingMatch() {
        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(anyString(), anyString(), any())).thenReturn(
                List.of());
        var foundMatches = matchService.findAllFilteredOrUnfilteredMatches(Optional.of(""),
                Optional.of(""), PageRequest.ofPage(10));

        assertTrue(foundMatches.isEmpty());
    }

    @Test
    void ensureFindAllFilteredOrUnfilteredMatchesReturnsExistingMatchForUserTokenNotBeingSpecified() {
        Match match = match();
        List<Match> matches = new ArrayList<>();
        matches.add(match);
        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(anyString(), any(), any())).thenReturn(matches);

        var foundMatches = matchService.findAllFilteredOrUnfilteredMatches(
                Optional.of(match.getToken()), Optional.empty(), PageRequest.ofPage(1));

        assertTrue(foundMatches.contains(match));
        assertEquals(foundMatches.size(), matches.size());
        assertEquals(match, foundMatches.stream().findFirst().get());

    }

    @Test
    void ensureFindAllFilteredOrUnfilteredMatchesReturnsExistingMatchForGameTokenNotBeingSpecified() {
        Match match = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);

        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(any(), anyString(), any())).thenReturn(matches);
        var foundMatches = matchService.findAllFilteredOrUnfilteredMatches(
                Optional.empty(), Optional.of(match.getUsers().getFirst().getToken()), PageRequest.ofPage(1));

        assertTrue(foundMatches.contains(match));
        assertEquals(foundMatches.size(), matches.size());
        assertEquals(match, foundMatches.stream().findFirst().get());
    }

    @Test
    void ensureFindAllFilteredOrUnfilteredMatchesReturnsAllMatchesForNoTokensBeingSpecified() {
        Match match = match();
        Match match1 = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);
        matches.add(match1);

        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(any(), any(), any())).thenReturn(matches);

        var foundMatches = matchService.findAllFilteredOrUnfilteredMatches(Optional.empty(),
                Optional.empty(), PageRequest.ofPage(1));

        assertEquals(matches.size(), foundMatches.size());
    }

    @Test
    void ensureCountAllFilteredOrUnfilteredMatchesReturnsCountOfAllMatches() {
        Match match = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);

        when(matchRepository.countMatchesFilteredAndUnfiltered(any(), any())).thenReturn((long) matches.size());

        assertEquals(matches.size(), matchService.countAllFilteredOrUnfilteredMatches(Optional.empty(), Optional.empty()));
    }

    @Test
    void ensureCountAllFilteredOrUnfilteredMatchesReturnsForGameTokenCountOfFilteredMatches() {
        Match match = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);

        when(matchRepository.countMatchesFilteredAndUnfiltered(any(), any())).thenReturn((long) matches.size());

        assertEquals(matches.size(), matchService.countAllFilteredOrUnfilteredMatches(Optional.of(match.getGame().getToken()), Optional.empty()));
    }

    @Test
    void ensureCountAllFilteredOrUnfilteredMatchesReturnsForUserTokenCountOfFilteredMatches() {
        Match match = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);

        when(matchRepository.countMatchesFilteredAndUnfiltered(any(), any())).thenReturn((long) matches.size());

        assertEquals(matches.size(), matchService.countAllFilteredOrUnfilteredMatches(Optional.empty(),
                Optional.of(match.getUsers().getFirst().getToken())));
    }

    @Test
    void ensureCountAllFilteredOrUnfilteredMatchesReturnsForGameTokenAndUserTokenCountOfFilteredMatches() {
        Match match = match();
        match.setUsers(TestFixtures.usersWithId(1));
        List<Match> matches = new ArrayList<>();
        matches.add(match);

        when(matchRepository.countMatchesFilteredAndUnfiltered(any(), any())).thenReturn((long) matches.size());

        assertEquals(matches.size(), matchService.countAllFilteredOrUnfilteredMatches(
                Optional.of(match.getGame().getToken()),
                Optional.of(match.getUsers().getFirst().getToken())));
    }
}
