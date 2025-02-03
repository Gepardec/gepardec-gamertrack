package com.gepardec.core.repository;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    Optional<Score> findScoreByToken(String token);
    List<Score> filterScores(Double minPoints, Double maxPoints, String userToken, String gameToken,Boolean includeDeactivatedUsers);
    List<Score> findTopScoreByGame(String gameToken, int top, Boolean includeDeactivatedUsers);
    List<Score> findScoreByScorePoints(double scorePoints, Boolean includeDeactivatedUsers);
    Optional<Score> saveScore(Score scoreDto);
    Optional<Score> updateScore(Score scoreDto);
    void deleteScore(Score score);
    boolean scoreExists(Score scoreDto);
}
