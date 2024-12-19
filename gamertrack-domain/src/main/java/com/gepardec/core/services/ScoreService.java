package com.gepardec.core.services;

import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId);
    List<Score> findScoresByUser(Long userId);
    List<Score> findScoresByGame(Long gameId);
    List<Score> findTopScoresByGame(Long gameId, int top);
    List<Score> findScoreByScoresPoints(double scorePoints);
    List<Score> findScoreByMinMaxScoresPoints(double minPoints, double maxPoints);
    Optional<Score> saveScore(ScoreDto scoreDto);
    Optional<Score> updateScore(ScoreDto scoreDto);
    boolean scoreExists(ScoreDto scoreDto);
}
