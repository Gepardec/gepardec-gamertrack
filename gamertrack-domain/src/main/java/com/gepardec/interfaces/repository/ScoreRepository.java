package com.gepardec.interfaces.repository;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    List<Score> findAllScores();
    Optional<Score> findById(Long id);
    List<Score> findByUser(Long userId);
    List<Score> findByGame(Long gameId);
    List<Score> findByScorePoints(double scorePoints);
    Optional<Score> saveScore(Score score);
}
