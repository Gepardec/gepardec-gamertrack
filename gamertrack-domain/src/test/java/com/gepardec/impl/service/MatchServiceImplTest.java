package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.EloService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Match;
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
  void ensureFindMatchByGameTokenAndUserTokenReturnsListOfMatchesForExistingMatch() {
    List<Match> matches = TestFixtures.matches(5);
    when(matchRepository.findMatchesByGameTokenAndUserToken(anyString(), anyString())).thenReturn(
        matches);

    var foundMatches = matchService.findMatchesByGameTokenAndUserToken(Optional.of(""),
        Optional.of(""));
    assertTrue(matches.contains(matches.getFirst()));
    assertEquals(matches.size(), foundMatches.size());
  }

  @Test
  void ensureFindMatchByGameTokenAndUserTokenReturnsEmptyListForNonExistingMatch() {
    when(matchRepository.findMatchesByGameTokenAndUserToken(anyString(), anyString())).thenReturn(
        List.of());
    var foundMatches = matchService.findMatchesByGameTokenAndUserToken(Optional.of(""),
        Optional.of(""));

    assertTrue(foundMatches.isEmpty());
  }

  @Test
  void ensureFindMatchByGameTokenAndUserTokenReturnsExistingMatchForUserTokenNotBeingSpecified() {
    Match match = match();
    List<Match> matches = new ArrayList<>();
    matches.add(match);
    when(matchRepository.findMatchesByGameToken(anyString())).thenReturn(matches);

    var foundMatches = matchService.findMatchesByGameTokenAndUserToken(
        Optional.of(match.getToken()), Optional.empty());

    assertTrue(foundMatches.contains(match));
    assertEquals(foundMatches.size(), matches.size());
    assertEquals(match, foundMatches.stream().findFirst().get());

  }

  @Test
  void ensureFindMatchByGameTokenAndUserTokenReturnsExistingMatchForGameTokenNotBeingSpecified() {
    Match match = match();
    match.setUsers(TestFixtures.usersWithId(1));
    List<Match> matches = new ArrayList<>();
    matches.add(match);

    when(matchRepository.findMatchesByUserToken(anyString())).thenReturn(matches);
    var foundMatches = matchService.findMatchesByGameTokenAndUserToken(
        Optional.empty(), Optional.of(match.getUsers().getFirst().getToken()));

    assertTrue(foundMatches.contains(match));
    assertEquals(foundMatches.size(), matches.size());
    assertEquals(match, foundMatches.stream().findFirst().get());
  }

  @Test
  void ensureFindMatchByGameTokenAndUserTokenReturnsEmptyListForBothEmptyParameters() {

    var foundMatches = matchService.findMatchesByGameTokenAndUserToken(Optional.empty(),
        Optional.empty());

    assertTrue(foundMatches.isEmpty());
  }
}
