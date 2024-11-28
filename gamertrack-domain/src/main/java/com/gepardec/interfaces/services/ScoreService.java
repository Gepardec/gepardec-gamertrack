package com.gepardec.interfaces.services;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores();
    Optional<Score> findById(Long id);
    List<Score> findByUser(Long userId);
    List<Score> findByGame(Long gameId);
    List<Score> findByScorePoints(double scorePoints);
    Optional<Score> saveScore(Score score);
    Optional<Score> updateScore(Long id, Score scoreEdit);
    Optional<Score> deleteScore(Long id);

}
