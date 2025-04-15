package com.gepardec.impl.service;

import com.gepardec.core.services.EloService;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Stateless
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

    public List<Score> updateElo(Game game, final List<Score> scoreList, List<User> playerOrder) {

        int numPlayers = playerOrder.size();
        List<Score> results = new ArrayList<>();
        List<Score> UpdatedScoreList = new ArrayList<>();
        for( Score score : scoreList ) {
            UpdatedScoreList.add(new Score(score.getId(),score.getUser(),score.getGame(),score.getScorePoints(),score.getToken(),false));
        }

        for (int i = 0; i < numPlayers; i++) {
            User user = playerOrder.get(i);
            double score = 1.0 - (i / (double) (numPlayers - 1));
            results.add(new Score(0L,user, game, score,"",false));
        }

        for (int i = 0; i < numPlayers; i++) {
            User user = playerOrder.get(i);
            double playerRating = scoreList.stream().filter(x -> x.getUser().getToken() == user.getToken()).toList().getFirst().getScorePoints();

            double totalExpectedProbability = 0.0;
            for (int j = 0; j < numPlayers; j++) {
                if (i != j) {
                    User opponent = playerOrder.get(j);
                    double opponentRating = scoreList.stream().filter(x -> x.getUser().getToken() == opponent.getToken()).toList().getFirst().getScorePoints();
                    totalExpectedProbability += expectedProbability(playerRating, opponentRating);

                }
            }

            double result = results.stream().filter(x -> x.getUser().getToken() == user.getToken()).toList().getFirst().getScorePoints();
            double expectedResult = totalExpectedProbability / (numPlayers - 1);

            double newRating = calculateNewScore(playerRating, expectedResult, result);

            UpdatedScoreList.get(i).setScorePoints(newRating);
            UpdatedScoreList.get(i).setDeletable(false);
        }
        return UpdatedScoreList;
    }
}
