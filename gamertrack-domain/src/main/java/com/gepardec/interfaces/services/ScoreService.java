package com.gepardec.interfaces.services;

import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> findScoreByUser(Long userId);
    List<Score> findScoreByGame(Long gameId);
    List<Score> findTopScoreByGame(Long gameId, int top);
    List<Score> findScoreByScorePoints(double scorePoints);
    List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints);
    Optional<Score> saveScore(ScoreDto scoreDto);
    Optional<Score> updateScore(ScoreDto scoreDto);
    boolean scoreExists(ScoreDto scoreDto);
}
