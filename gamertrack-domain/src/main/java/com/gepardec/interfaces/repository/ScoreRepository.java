package com.gepardec.interfaces.repository;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> findScoreByUser(Long userId);
    List<Score> findScoreByGame(Long gameId);
    List<Score> findScoreByScorePoints(double scorePoints);
    Optional<Score> saveScore(Long userId, Long gameId, double scorePoints);
    boolean scoreExists(Long userId, Long gameId);
}
