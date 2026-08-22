package com.gepardec.impl.service;

import com.gepardec.core.services.EloService;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
public class EloServiceImpl implements EloService {

    private static final int K = 32;

    @Override
    public double expectedProbability(double playerScore, double opponentScore) {
        return 1.0 / (1 + Math.pow(10, (opponentScore - playerScore) / 400.0));

    }

    @Override
    public double calculateNewScore(double oldScore, double expectedProbability, double result) {
        return (int) Math.round(oldScore + K * (result - expectedProbability));
    }

    public List<Score> updateElo(Game game, final List<Score> scoreList, Map<String, Integer> placements) {

        int numPlayers = scoreList.size();
        List<Score> UpdatedScoreList = new ArrayList<>();
        for( Score score : scoreList ) {
            UpdatedScoreList.add(new Score(score.getId(),score.getUser(),score.getGame(),score.getScorePoints(),score.getToken(),false));
        }

        Map<Integer, Long> tiedPlayersPerPlacement = placements.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (int i = 0; i < numPlayers; i++) {
            Score playerScore = scoreList.get(i);
            double playerRating = playerScore.getScorePoints();

            double totalExpectedProbability = 0.0;
            for (int j = 0; j < numPlayers; j++) {
                if (i != j) {
                    totalExpectedProbability += expectedProbability(playerRating, scoreList.get(j).getScorePoints());
                }
            }

            //tied players share the result of the positions they occupy together (average of those positions' results)
            int placement = placements.get(playerScore.getUser().getToken());
            long tiedPlayers = tiedPlayersPerPlacement.get(placement);
            double result = 1.0 - (placement - 1 + (tiedPlayers - 1) / 2.0) / (numPlayers - 1);
            double expectedResult = totalExpectedProbability / (numPlayers - 1);

            double newRating = calculateNewScore(playerRating, expectedResult, result);

            UpdatedScoreList.get(i).setScorePoints(newRating);
            UpdatedScoreList.get(i).setDeletable(false);
        }
        return UpdatedScoreList;
    }
}
