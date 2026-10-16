package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.HeadToHead;
import com.gepardec.model.Match;
import com.gepardec.model.MatchOutcome;
import com.gepardec.model.PlayerForm;
import com.gepardec.model.PlayerGameStats;
import com.gepardec.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gepardec.model.MatchOutcome.DRAW;
import static com.gepardec.model.MatchOutcome.LOSS;
import static com.gepardec.model.MatchOutcome.WIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    MatchRepository matchRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    GameRepository gameRepository;

    @InjectMocks
    StatisticsServiceImpl statisticsService;

    private final Game game = TestFixtures.game();
    private final User userA = new User(1L, "Alice", "Anderson", false, "user-a");
    private final User userB = new User(2L, "Bob", "Baker", false, "user-b");
    private final User userC = new User(3L, "Cleo", "Curtis", false, "user-c");
    private final User userD = new User(4L, "Dana", "Doe", false, "user-d");

    private Match matchWithOrder(long id, User... usersInPlacementOrder) {
        return TestFixtures.match(id, game, List.of(usersInPlacementOrder));
    }

    private void mockExistingUserAndGame(User... users) {
        for (User user : users) {
            when(userRepository.findUserByToken(user.getToken())).thenReturn(Optional.of(user));
        }
        when(gameRepository.findGameByToken(game.getToken())).thenReturn(Optional.of(game));
    }

    private void mockMatchesNewestFirst(String userToken, List<Match> matchesNewestFirst) {
        when(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(
                eq(game.getToken()), eq(userToken), any())).thenReturn(matchesNewestFirst);
    }

    @Test
    void ensureStatsAreComputedForKnownMatchHistory() {
        List<Match> matchesNewestFirst = new ArrayList<>();
        long id = 9;
        for (int i = 0; i < 3; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userB, userA));
        }
        for (int i = 0; i < 6; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userA, userB));
        }

        mockExistingUserAndGame(userA);
        mockMatchesNewestFirst(userA.getToken(), matchesNewestFirst);

        PlayerGameStats stats =
                statisticsService.getPlayerGameStats(userA.getToken(), game.getToken()).orElseThrow();

        assertEquals(9, stats.matchesPlayed());
        assertEquals(6, stats.wins());
        assertEquals(0, stats.draws());
        assertEquals(3, stats.losses());
        assertEquals(6.0 / 9.0, stats.winRate(), 1e-9);
        assertEquals(LOSS, stats.currentStreak().type());
        assertEquals(3, stats.currentStreak().length());
        assertEquals(6, stats.longestWinStreak());
        assertEquals(0, stats.excludedMatches());
    }

    @Test
    void ensureStatsCountDrawsAndCorrectWinRate() {
        List<MatchOutcome> outcomesNewestFirst = new ArrayList<>();
        outcomesNewestFirst.addAll(Collections.nCopies(6, WIN));
        outcomesNewestFirst.add(DRAW);
        outcomesNewestFirst.addAll(Collections.nCopies(3, LOSS));

        PlayerGameStats stats = statisticsService.buildPlayerGameStats(
                userA.getToken(), game.getToken(), outcomesNewestFirst, 0);

        assertEquals(10, stats.matchesPlayed());
        assertEquals(6, stats.wins());
        assertEquals(1, stats.draws());
        assertEquals(3, stats.losses());
        assertEquals(0.6, stats.winRate(), 1e-9);
    }

    @Test
    void ensureThreeWinsInARowYieldWinStreakOfThree() {
        PlayerGameStats stats = statisticsService.buildPlayerGameStats(
                userA.getToken(), game.getToken(), List.of(WIN, WIN, WIN), 0);

        assertEquals(WIN, stats.currentStreak().type());
        assertEquals(3, stats.currentStreak().length());
        assertEquals(3, stats.longestWinStreak());
    }

    @Test
    void ensureLossAfterWinStreakYieldsLossStreakOfOneAndKeepsLongestWinStreak() {
        PlayerGameStats stats = statisticsService.buildPlayerGameStats(
                userA.getToken(), game.getToken(), List.of(LOSS, WIN, WIN, WIN), 0);

        assertEquals(LOSS, stats.currentStreak().type());
        assertEquals(1, stats.currentStreak().length());
        assertEquals(3, stats.longestWinStreak());
    }

    @Test
    void ensureFormReturnsOnlyTheRequestedNumberOfNewestResults() {
        List<Match> matchesNewestFirst = new ArrayList<>();
        long id = 8;
        for (int i = 0; i < 3; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userB, userA));
        }
        for (int i = 0; i < 5; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userA, userB));
        }

        mockExistingUserAndGame(userA);
        mockMatchesNewestFirst(userA.getToken(), matchesNewestFirst);

        PlayerForm form =
                statisticsService.getPlayerForm(userA.getToken(), game.getToken(), 5).orElseThrow();

        assertEquals(List.of(LOSS, LOSS, LOSS, WIN, WIN), form.results());
    }

    @Test
    void ensureFormWithLimitLargerThanMatchCountReturnsAllResults() {
        List<Match> matchesNewestFirst = new ArrayList<>();
        for (long id = 8; id > 0; id--) {
            matchesNewestFirst.add(matchWithOrder(id, userA, userB));
        }

        mockExistingUserAndGame(userA);
        mockMatchesNewestFirst(userA.getToken(), matchesNewestFirst);

        PlayerForm form =
                statisticsService.getPlayerForm(userA.getToken(), game.getToken(), 50).orElseThrow();

        assertEquals(8, form.results().size());
        assertTrue(form.results().stream().allMatch(WIN::equals));
    }

    @Test
    void ensureHeadToHeadCountsMutualMatchesAndIsSymmetricallyConsistent() {
        List<Match> matchesNewestFirst = new ArrayList<>();
        long id = 11;
        for (int i = 0; i < 7; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userA, userB));
        }
        for (int i = 0; i < 3; i++) {
            matchesNewestFirst.add(matchWithOrder(id--, userB, userA));
        }
        // match against someone else must not count for the record
        matchesNewestFirst.add(matchWithOrder(id, userA, userC));

        mockExistingUserAndGame(userA, userB);
        mockMatchesNewestFirst(userA.getToken(), matchesNewestFirst);
        mockMatchesNewestFirst(userB.getToken(), matchesNewestFirst.stream()
                .filter(match -> match.getUsers().contains(userB)).toList());

        HeadToHead headToHead = statisticsService
                .getHeadToHead(userA.getToken(), userB.getToken(), game.getToken()).orElseThrow();

        assertEquals(10, headToHead.matchesPlayed());
        assertEquals(7, headToHead.firstUserWins());
        assertEquals(3, headToHead.secondUserWins());
        assertEquals(0, headToHead.draws());

        HeadToHead mirrored = statisticsService
                .getHeadToHead(userB.getToken(), userA.getToken(), game.getToken()).orElseThrow();

        assertEquals(10, mirrored.matchesPlayed());
        assertEquals(3, mirrored.firstUserWins());
        assertEquals(7, mirrored.secondUserWins());
        assertEquals(0, mirrored.draws());
    }

    @Test
    void ensureHeadToHeadUsesPlacementComparisonInMultiplayerMatches() {
        Match fourPlayerMatch = matchWithOrder(1L, userC, userA, userD, userB);

        mockExistingUserAndGame(userA, userB);
        mockMatchesNewestFirst(userA.getToken(), List.of(fourPlayerMatch));

        HeadToHead headToHead = statisticsService
                .getHeadToHead(userA.getToken(), userB.getToken(), game.getToken()).orElseThrow();

        assertEquals(1, headToHead.matchesPlayed());
        assertEquals(1, headToHead.firstUserWins());
        assertEquals(0, headToHead.secondUserWins());
        assertEquals(0, headToHead.draws());
    }

    @Test
    void ensureSharedPlacementCountsAsDraw() {
        HeadToHead headToHead = statisticsService.buildHeadToHead(
                game.getToken(), userA.getToken(), userB.getToken(),
                List.of(new StatisticsServiceImpl.PlacementPair(1, 1),
                        new StatisticsServiceImpl.PlacementPair(0, 2)),
                0);

        assertEquals(2, headToHead.matchesPlayed());
        assertEquals(1, headToHead.firstUserWins());
        assertEquals(0, headToHead.secondUserWins());
        assertEquals(1, headToHead.draws());
    }

    @Test
    void ensurePlayerWithoutMatchesGetsZeroedStats() {
        mockExistingUserAndGame(userA);
        mockMatchesNewestFirst(userA.getToken(), List.of());

        PlayerGameStats stats =
                statisticsService.getPlayerGameStats(userA.getToken(), game.getToken()).orElseThrow();

        assertEquals(0, stats.matchesPlayed());
        assertEquals(0, stats.wins());
        assertEquals(0, stats.draws());
        assertEquals(0, stats.losses());
        assertEquals(0.0, stats.winRate());
        assertNull(stats.currentStreak());
        assertEquals(0, stats.longestWinStreak());
        assertEquals(0, stats.excludedMatches());
    }

    @Test
    void ensurePlayersWhoNeverMetGetEmptyHeadToHead() {
        mockExistingUserAndGame(userA, userB);
        mockMatchesNewestFirst(userA.getToken(), List.of(matchWithOrder(1L, userA, userC)));

        HeadToHead headToHead = statisticsService
                .getHeadToHead(userA.getToken(), userB.getToken(), game.getToken()).orElseThrow();

        assertEquals(0, headToHead.matchesPlayed());
        assertEquals(0, headToHead.firstUserWins());
        assertEquals(0, headToHead.secondUserWins());
        assertEquals(0, headToHead.draws());
    }

    @Test
    void ensureMatchesWithoutStoredResultAreExcludedAndReported() {
        Match withoutOpponents = matchWithOrder(3L, userA);
        Match withoutUsers = TestFixtures.match(2L, game, List.of());
        Match won = matchWithOrder(1L, userA, userB);

        mockExistingUserAndGame(userA);
        mockMatchesNewestFirst(userA.getToken(), List.of(withoutOpponents, withoutUsers, won));

        PlayerGameStats stats =
                statisticsService.getPlayerGameStats(userA.getToken(), game.getToken()).orElseThrow();

        assertEquals(1, stats.matchesPlayed());
        assertEquals(1, stats.wins());
        assertEquals(2, stats.excludedMatches());
        assertEquals(1.0, stats.winRate(), 1e-9);
    }

    @Test
    void ensureUnknownUserYieldsEmptyOptional() {
        when(userRepository.findUserByToken("unknown")).thenReturn(Optional.empty());

        assertTrue(statisticsService.getPlayerGameStats("unknown", game.getToken()).isEmpty());
        assertTrue(statisticsService.getPlayerForm("unknown", game.getToken(), 5).isEmpty());
        assertTrue(statisticsService.getHeadToHead("unknown", userB.getToken(), game.getToken()).isEmpty());
    }

    @Test
    void ensureUnknownGameYieldsEmptyOptional() {
        when(userRepository.findUserByToken(userA.getToken())).thenReturn(Optional.of(userA));
        when(gameRepository.findGameByToken("unknown")).thenReturn(Optional.empty());

        assertTrue(statisticsService.getPlayerGameStats(userA.getToken(), "unknown").isEmpty());
        assertTrue(statisticsService.getPlayerForm(userA.getToken(), "unknown", 5).isEmpty());
        assertTrue(statisticsService.getHeadToHead(userA.getToken(), userB.getToken(), "unknown").isEmpty());
    }
}
