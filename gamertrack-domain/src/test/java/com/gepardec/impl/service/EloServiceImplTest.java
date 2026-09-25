package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class EloServiceImplTest {

    @InjectMocks
    EloServiceImpl eloService;

    @Test
    void ensureExpectedProbabilityReturnsExpectedScoreWorks() {
        assertEquals(eloService.expectedProbability(1500,1500), 0.5);
    }

    @Test
    void ensureCalculateNewScoreFromSingleWinWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.5;
        double result = 1.0;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1516);
    }
    @Test
    void ensureCalculateNewScoreFromSingleLossWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.5;
        double result = 0.0;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1484);
    }
    @Test
    void ensureCalculateNewScoreFromSingleTieWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.5;
        double result = 0.5;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1500);
    }
    @Test
    void ensureCalculateNewScoreFromSingleWinWithHighProbabilityWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.75;
        double result = 1;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1508);
    }
    @Test
    void ensureCalculateNewScoreFromSingleLossWithLowProbabilityWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.25;
        double result = 0;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1492);
    }
    @Test
    void ensureCalculateNewScoreFromSingleLossWithHighProbabilityWorks() {
        int oldScore = 1500;
        double expectedProbability = 0.75;
        double result = 0;
        assertEquals(eloService.calculateNewScore(oldScore,expectedProbability,result), 1476);
    }

    @Test
    void ensureUpdatingEloWithTwoUsersWorks(){
        Game game = TestFixtures.game();

        User user1 = TestFixtures.user(0L);
        User user2 = TestFixtures.user(1L);
        User user3 = TestFixtures.user(1L);
        User user4 = TestFixtures.user(1L);
        User user5 = TestFixtures.user(1L);

        Score score1 = new Score(0L,user1,game,1500,"",true);
        Score score2 = new Score(0L,user2,game,1500,"",true);
        Score score3 = new Score(0L,user3,game,1500,"",true);
        Score score4 = new Score(0L,user4,game,1500,"",true);
        Score score5 = new Score(0L,user5,game,1500,"",true);

        List<Score> scoresList = List.of(score1,score2, score3, score4, score5);
        List<User> usersList = List.of(user1,user2, user3, user4, user5);

        List<Score> updatedScoresList = eloService.updateElo(game, scoresList,usersList);

        assertEquals(1516, updatedScoresList.get(0).getScorePoints());
        assertEquals(1508, updatedScoresList.get(1).getScorePoints());
        assertEquals(1500, updatedScoresList.get(2).getScorePoints());
        assertEquals(1492, updatedScoresList.get(3).getScorePoints());
        assertEquals(1484, updatedScoresList.get(4).getScorePoints());


    }

    @Test
    void ensureUpdatingEloWorksWithDistinctUserInstancesCarryingEqualTokens() {
        Game game = TestFixtures.game();

        User winner = new User(1L, "Max", "Muster", false, "elo-user-token-1");
        User loser = new User(2L, "Jakob", "Mayer", false, "elo-user-token-2");

        // score users are distinct instances whose tokens are equal but not identical,
        // so a reference comparison on the token can never match
        Score winnerScore = new Score(1L, new User(1L, "Max", "Muster", false, new String("elo-user-token-1")), game, 1500, "", true);
        Score loserScore = new Score(2L, new User(2L, "Jakob", "Mayer", false, new String("elo-user-token-2")), game, 1500, "", true);

        List<Score> updatedScoresList = eloService.updateElo(game, List.of(winnerScore, loserScore), List.of(winner, loser));

        assertEquals(1516, scorePointsOf(updatedScoresList, winner));
        assertEquals(1484, scorePointsOf(updatedScoresList, loser));
    }

    @Test
    void ensureUpdatingEloAssignsRatingsByUserWhenScoreListOrderDiffersFromPlayerOrder() {
        Game game = TestFixtures.game();

        User winner = TestFixtures.user(1L);
        User loser = TestFixtures.user(2L);

        Score winnerScore = new Score(1L, winner, game, 1500, "", true);
        Score loserScore = new Score(2L, loser, game, 1500, "", true);

        List<Score> updatedScoresList = eloService.updateElo(game, List.of(loserScore, winnerScore), List.of(winner, loser));

        assertEquals(1516, scorePointsOf(updatedScoresList, winner));
        assertEquals(1484, scorePointsOf(updatedScoresList, loser));
    }

    @Test
    void ensureUpdatingEloWithTwoEquallyRatedPlayersGrantsAndDeductsSameAmount() {
        Game game = TestFixtures.game();

        User winner = TestFixtures.user(1L);
        User loser = TestFixtures.user(2L);

        Score winnerScore = new Score(1L, winner, game, 1500, "", true);
        Score loserScore = new Score(2L, loser, game, 1500, "", true);

        List<Score> updatedScoresList = eloService.updateElo(game, List.of(winnerScore, loserScore), List.of(winner, loser));

        assertEquals(1516, scorePointsOf(updatedScoresList, winner));
        assertEquals(1484, scorePointsOf(updatedScoresList, loser));
    }

    @Test
    void ensureUpdatingEloFailsWithDescriptiveErrorWhenScoreForPlayerIsMissing() {
        Game game = TestFixtures.game();

        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);

        Score score1 = new Score(1L, user1, game, 1500, "", true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eloService.updateElo(game, List.of(score1), List.of(user1, user2)));

        assertTrue(exception.getMessage().contains(user2.getToken()));
    }

    private static double scorePointsOf(List<Score> scores, User user) {
        return scores.stream()
                .filter(score -> user.getToken().equals(score.getUser().getToken()))
                .findFirst()
                .orElseThrow()
                .getScorePoints();
    }

}
