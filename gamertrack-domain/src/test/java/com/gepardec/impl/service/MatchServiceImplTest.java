package com.gepardec.impl.service;

import static com.gepardec.TestFixtures.matches;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

  @Mock
  MatchRepository matchRepository;

  @Mock
  UserRepository userRepository;
  @Mock
  GameRepository gameRepository;

  @InjectMocks
  MatchServiceImpl matchService;


  @Test
  void ensureSavingValidMatchReturnsOptionalMatch() {
    Match match = TestFixtures.matchToMatchDto(
        TestFixtures.match());

    when(matchRepository.saveMatch(any())).thenReturn(
        Optional.of(TestFixtures.match()));
    when(gameRepository.existsByGameId(anyLong())).thenReturn(true);
    when(userRepository.existsByUserId(anyList())).thenReturn(true);

    assertEquals(matchService.saveMatch(match).get().getId(),
        TestFixtures.match().getId());
  }

  @Test
  void ensureSavingInvalidMatchReferencingNotExistingGameReturnsEmptyOptional() {
    //Given
    Match match = TestFixtures.match();
    match.setUsers(TestFixtures.users(10));

    //When
    var savedMatch = matchService.saveMatch(
        TestFixtures.matchToMatchDto(match));

    //Then
    assertEquals(Optional.empty(), savedMatch);
  }

  @Test
  void ensureSavingInvalidMatchReferencingNoUsersReturnsEmptyOptional() {
    //Given
    Match match = TestFixtures.match();

    //When
    var savedMatch = matchService.saveMatch(
        TestFixtures.matchToMatchDto(match));

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
  void ensureFindMatchByIdReturnsMatchForExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchById(any())).thenReturn(Optional.of(match));

    assertEquals(match, matchService.findMatchById(match.getId()).get());
  }

  @Test
  void ensureFindMatchByIdReturnsOptionalEmptyForNonExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchById(any())).thenReturn(Optional.empty());

    assertEquals(Optional.empty(), matchService.findMatchById(match.getId()));
  }

  @Test
  void ensureDeleteMatchReturnsDeletedMatchForExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchById(any())).thenReturn(Optional.of(match));
    var deletedMatch = matchService.deleteMatch(match.getId());

    assertEquals(match, deletedMatch.get());
  }

  @Test
  void ensureDeleteMatchReturnsOptionalEmptyForNonExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchById(any())).thenReturn(Optional.empty());

    var deletedMatch = matchService.deleteMatch(match.getId());

    assertEquals(Optional.empty(), deletedMatch);
  }

  @Test
  void ensureUpdateMatchReturnsUpdatedMatchForExistingMatch() {
    //Given
    Match matchNew = TestFixtures.match(1L);
    Match matchNewDto = TestFixtures.matchToMatchDto(matchNew);

    //When

    when(matchRepository.updateMatch(any())).thenReturn(Optional.of(matchNew));
    when(gameRepository.existsByGameId(anyLong())).thenReturn(true);
    when(userRepository.existsByUserId(anyList())).thenReturn(true);
    when(matchRepository.existsMatchById(anyLong())).thenReturn(true);

    var updatedMatch = matchService.updateMatch(matchNewDto);

    //Then
    assertEquals(matchNew.getId(), updatedMatch.get().getId());
  }

  @Test
  void ensureFindMatchByUserIdReturnsListOfMatchesForExistingMatchWithUserId() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchesByUserId(anyLong())).thenReturn(
        List.of(match));

    var foundMatches = matchService.findMatchesByUserId(
        match.getUsers().getFirst().getId());

    assertTrue(foundMatches.contains(match));
  }

  @Test
  void ensureFindMatchByUserIdReturnsEmptyListForNonExistingMatch() {
    Match match = TestFixtures.match();
    when(matchRepository.findMatchesByUserId(anyLong())).thenReturn(List.of());

    var foundMatches = matchService.findMatchesByUserId(
        match.getUsers().getFirst().getId());

    assertTrue(foundMatches.isEmpty());
  }


  @Test
  void ensureFindMatchByGameIdReturnsListOfMatchesForExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchesByGameId(anyLong())).thenReturn(List.of(match));

    var matches = matchService.findMatchesByGameId(match.getGame().getId());

    assertTrue(matches.contains(match));
  }

  @Test
  void ensureFindMatchByGameIdReturnsEmptyListForNonExistingMatch() {
    Match match = TestFixtures.match();

    when(matchRepository.findMatchesByGameId(anyLong())).thenReturn(List.of());

    var foundGameOutcomes = matchService.findMatchesByGameId(
        match.getGame().getId());

    assertTrue(foundGameOutcomes.isEmpty());

  }

  @Test
  void ensureFindMatchByUserIdAndGameIdReturnsListOfMatchesForExistingMatch() {
    List<Match> matches = TestFixtures.matches(5);
    when(matchRepository.findMatchesByUserIdAndGameId(anyLong(), anyLong())).thenReturn(matches);

    var foundMatches = matchService.findMatchesByUserIdAndGameId(Optional.of(1L), Optional.of(2L));
    assertTrue(matches.contains(matches.get(0)));
    assertEquals(matches.size(), foundMatches.size());
  }

  @Test
  void ensureFindMatchByUserIdAndGameIdReturnsEmptyListForNonExistingMatch() {
    List<Match> matches = TestFixtures.matches(5);
    when(matchRepository.findMatchesByUserIdAndGameId(anyLong(), anyLong())).thenReturn(List.of());
    var foundMatches = matchService.findMatchesByUserIdAndGameId(Optional.of(1L), Optional.of(2L));

    assertTrue(foundMatches.isEmpty());
  }

  @Test
  void ensureFindMatchByUserIdAndGameIdReturnsExistingMatchForUserIdNotBeingSpecified() {
    Match match = TestFixtures.match();
    List<Match> matches = new ArrayList<>();
    matches.add(match);
    when(matchRepository.findMatchesByGameId(anyLong())).thenReturn(matches);

    var foundMatches = matchService.findMatchesByUserIdAndGameId(Optional.empty(),
        Optional.of(match.getId()));

    assertTrue(foundMatches.contains(match));
    assertEquals(foundMatches.size(), matches.size());
    assertEquals(match, foundMatches.stream().findFirst().get());

  }

  @Test
  void ensureFindMatchByUserIdAndGameIdReturnsExistingMatchForGameIdNotBeingSpecified() {
    Match match = TestFixtures.match();
    List<Match> matches = new ArrayList<>();
    matches.add(match);

    when(matchRepository.findMatchesByUserId(anyLong())).thenReturn(matches);

    var foundmatches = matchService.findMatchesByUserIdAndGameId(
        Optional.of(match.getUsers().getFirst().getId()),
        Optional.empty());

    assertTrue(foundmatches.contains(match));
    assertEquals(foundmatches.size(), matches.size());
    assertEquals(match, foundmatches.stream().findFirst().get());
  }

  @Test
  void ensureFindMatchByUserIdAndGameReturnsEmptyListForBothEmptyParameters() {

    var foundMatches = matchService.findMatchesByUserIdAndGameId(Optional.empty(),
        Optional.empty());

    assertTrue(foundMatches.isEmpty());
  }
}
