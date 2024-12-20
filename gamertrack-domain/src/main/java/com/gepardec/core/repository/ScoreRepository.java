package com.gepardec.core.repository;

import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId);
    List<Score> findTopScoreByGame(Long gameId, int top);
    List<Score> findScoreByScorePoints(double scorePoints);
    Optional<Score> saveScore(ScoreDto scoreDto);
    Optional<Score> updateScore(ScoreDto scoreDto);
    boolean scoreExists(ScoreDto scoreDto);
}
