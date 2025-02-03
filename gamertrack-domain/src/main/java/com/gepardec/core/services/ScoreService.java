package com.gepardec.core.services;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores(Boolean includeDeactivated);
    Optional<Score> findScoreByToken(String token);
    List<Score> filterScores(Double minPoints, Double maxPoints, String userToken, String gameToken, Boolean includeDeactivatedUsers);
    List<Score> findScoresByUser(String userToken, Boolean includeDeactivatedUsers);
    List<Score> findScoresByGame(String gameToken, Boolean includeDeactivatedUsers);
    List<Score> findTopScoresByGame(String gameToken, int top, Boolean includeDeactivatedUsers);
    List<Score> findScoreByScoresPoints(double scorePoints, Boolean includeDeactivatedUsers);
    List<Score> findScoreByMinMaxScoresPoints(double minPoints, double maxPoints, Boolean includeDeactivatedUsers);
    Optional<Score> saveScore(Score scoreDto);
    Optional<Score> updateScore(Score scoreDto);
    void deleteScore(String token);
    boolean scoreExists(Score scoreDto);
}
