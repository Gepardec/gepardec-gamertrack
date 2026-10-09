package com.gepardec.core.services;

import com.gepardec.model.Game;
import com.gepardec.model.Score;

import java.util.List;
import java.util.Map;

public interface EloService {
    double expectedProbability(double playerScore, double opponentScore);
    double calculateNewScore(double oldScore, double expectedProbability, double result);
    List<Score> updateElo(Game game, List<Score> ScoreList, Map<String, Integer> placements);
}
