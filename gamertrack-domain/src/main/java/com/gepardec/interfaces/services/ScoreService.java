package com.gepardec.interfaces.services;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    Optional<Score> saveScore(Score score);
    void deleteScore(Score score);
    List<Score> findAllScores();
    List<Score> findByUser(Long userId);
    List<Score> findByGame(Long gameId);
    List<Score> findByScorePoints(Long id);
}
