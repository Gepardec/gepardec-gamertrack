package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.StatisticsService;
import com.gepardec.model.HeadToHead;
import com.gepardec.model.Match;
import com.gepardec.model.MatchOutcome;
import com.gepardec.model.PlayerForm;
import com.gepardec.model.PlayerGameStats;
import com.gepardec.model.Streak;
import com.gepardec.model.User;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Computes read-only player statistics from stored matches.
 * <p>
 * A match result is derived from the persisted order of the match's user list
 * (first place first, as already assumed by the Elo calculation). A match only
 * counts for statistics if a result can be derived from it, meaning it has at
 * least two participants; all other matches are excluded and reported via the
 * excludedMatches count.
 */
@ApplicationScoped
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    private final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Inject
    private MatchRepository matchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private GameRepository gameRepository;

    @Override
    public Optional<PlayerGameStats> getPlayerGameStats(String userToken, String gameToken) {
        if (userOrGameMissing(userToken, gameToken)) {
            return Optional.empty();
        }

        logger.info("Computing stats for userToken %s and gameToken %s".formatted(userToken, gameToken));

        List<Match> matches = findMatchesNewestFirst(userToken, gameToken);
        List<MatchOutcome> outcomes = outcomesNewestFirst(matches, userToken);

        return Optional.of(
                buildPlayerGameStats(userToken, gameToken, outcomes, countMatchesWithoutResult(matches)));
    }

    @Override
    public Optional<PlayerForm> getPlayerForm(String userToken, String gameToken, int maxResults) {
        if (userOrGameMissing(userToken, gameToken)) {
            return Optional.empty();
        }

        logger.info("Computing form for userToken %s and gameToken %s with maxResults %s"
                .formatted(userToken, gameToken, maxResults));

        List<Match> matches = findMatchesNewestFirst(userToken, gameToken);
        List<MatchOutcome> outcomes = outcomesNewestFirst(matches, userToken).stream()
                .limit(Math.max(0, maxResults))
                .toList();

        return Optional.of(
                new PlayerForm(userToken, gameToken, outcomes, countMatchesWithoutResult(matches)));
    }

    @Override
    public Optional<HeadToHead> getHeadToHead(String firstUserToken, String secondUserToken, String gameToken) {
        if (userOrGameMissing(firstUserToken, gameToken)
                || userRepository.findUserByToken(secondUserToken).isEmpty()) {
            return Optional.empty();
        }

        logger.info("Computing head-to-head for userTokens %s and %s and gameToken %s"
                .formatted(firstUserToken, secondUserToken, gameToken));

        List<Match> mutualMatches = findMatchesNewestFirst(firstUserToken, gameToken).stream()
                .filter(match -> placementOf(match, secondUserToken).isPresent())
                .toList();

        List<PlacementPair> placements = mutualMatches.stream()
                .filter(StatisticsServiceImpl::hasStoredResult)
                .map(match -> new PlacementPair(
                        placementOf(match, firstUserToken).orElseThrow(),
                        placementOf(match, secondUserToken).orElseThrow()))
                .toList();

        return Optional.of(buildHeadToHead(gameToken, firstUserToken, secondUserToken, placements,
                countMatchesWithoutResult(mutualMatches)));
    }

    PlayerGameStats buildPlayerGameStats(String userToken, String gameToken,
                                         List<MatchOutcome> outcomesNewestFirst, long excludedMatches) {
        long wins = outcomesNewestFirst.stream().filter(MatchOutcome.WIN::equals).count();
        long draws = outcomesNewestFirst.stream().filter(MatchOutcome.DRAW::equals).count();
        long losses = outcomesNewestFirst.stream().filter(MatchOutcome.LOSS::equals).count();
        long played = outcomesNewestFirst.size();

        return new PlayerGameStats(userToken, gameToken, played, wins, draws, losses,
                played == 0 ? 0.0 : (double) wins / played,
                currentStreak(outcomesNewestFirst),
                longestWinStreak(outcomesNewestFirst),
                excludedMatches);
    }

    HeadToHead buildHeadToHead(String gameToken, String firstUserToken, String secondUserToken,
                               List<PlacementPair> placements, long excludedMatches) {
        long firstUserWins = placements.stream()
                .filter(pair -> pair.firstPlacement() < pair.secondPlacement()).count();
        long secondUserWins = placements.stream()
                .filter(pair -> pair.secondPlacement() < pair.firstPlacement()).count();
        long draws = placements.size() - firstUserWins - secondUserWins;

        return new HeadToHead(gameToken, firstUserToken, secondUserToken, placements.size(),
                firstUserWins, secondUserWins, draws, excludedMatches);
    }

    private boolean userOrGameMissing(String userToken, String gameToken) {
        return userRepository.findUserByToken(userToken).isEmpty()
                || gameRepository.findGameByToken(gameToken).isEmpty();
    }

    private List<Match> findMatchesNewestFirst(String userToken, String gameToken) {
        return matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(gameToken, userToken,
                PageRequest.ofPage(1L, Integer.MAX_VALUE, true));
    }

    private static List<MatchOutcome> outcomesNewestFirst(List<Match> matchesNewestFirst, String userToken) {
        return matchesNewestFirst.stream()
                .filter(StatisticsServiceImpl::hasStoredResult)
                .map(match -> outcomeFor(match, userToken))
                .flatMap(Optional::stream)
                .toList();
    }

    private static long countMatchesWithoutResult(List<Match> matches) {
        return matches.stream().filter(match -> !hasStoredResult(match)).count();
    }

    private static boolean hasStoredResult(Match match) {
        return match.getUsers() != null && match.getUsers().size() >= 2;
    }

    private static Optional<MatchOutcome> outcomeFor(Match match, String userToken) {
        OptionalInt placement = placementOf(match, userToken);
        if (placement.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(placement.getAsInt() == 0 ? MatchOutcome.WIN : MatchOutcome.LOSS);
    }

    private static OptionalInt placementOf(Match match, String userToken) {
        List<User> users = match.getUsers();
        if (users == null) {
            return OptionalInt.empty();
        }
        for (int i = 0; i < users.size(); i++) {
            if (userToken.equals(users.get(i).getToken())) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    private static Streak currentStreak(List<MatchOutcome> outcomesNewestFirst) {
        if (outcomesNewestFirst.isEmpty()) {
            return null;
        }
        MatchOutcome type = outcomesNewestFirst.getFirst();
        int length = 0;
        for (MatchOutcome outcome : outcomesNewestFirst) {
            if (outcome != type) {
                break;
            }
            length++;
        }
        return new Streak(type, length);
    }

    private static int longestWinStreak(List<MatchOutcome> outcomes) {
        int longest = 0;
        int current = 0;
        for (MatchOutcome outcome : outcomes) {
            current = outcome == MatchOutcome.WIN ? current + 1 : 0;
            longest = Math.max(longest, current);
        }
        return longest;
    }

    record PlacementPair(int firstPlacement, int secondPlacement) {
    }
}
