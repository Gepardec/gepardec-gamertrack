package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.EloService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Match;
import jakarta.data.page.PageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.when;


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
