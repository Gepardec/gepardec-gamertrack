package com.gepardec.core.services;

import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;

import java.util.List;

public interface EloService {
    double expectedProbability(double playerScore, double opponentScore);
    double calculateNewScore(double oldScore, double expectedProbability, double result);
    List<Score> updateElo(Game game, List<Score> ScoreList, List<User> playerOrder);
}
