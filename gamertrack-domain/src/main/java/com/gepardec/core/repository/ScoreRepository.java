package com.gepardec.core.repository;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    Optional<Score> findScoreById(Long id);
    List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId,Boolean includeDeactivatedUsers);
    List<Score> findTopScoreByGame(Long gameId, int top, Boolean includeDeactivatedUsers);
    List<Score> findScoreByScorePoints(double scorePoints, Boolean includeDeactivatedUsers);
    Optional<Score> saveScore(Score scoreDto);
    Optional<Score> updateScore(Score scoreDto);
    boolean scoreExists(Score scoreDto);
}
