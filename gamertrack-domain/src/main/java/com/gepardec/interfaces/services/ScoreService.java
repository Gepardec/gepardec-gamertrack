package com.gepardec.interfaces.services;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> findScoreByUser(Long userId);
    List<Score> findScoreByGame(Long gameId);
    List<Score> findScoreByScorePoints(double scorePoints);
    List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints);
    Optional<Score> saveScore(Long userId, Long gameId, double scorePoints);
    Optional<Score> updateScore(Long id, Score scoreEdit);
    boolean scoreExists(Long userId, Long gameId);
}
