package com.gepardec.interfaces.repository;

import com.gepardec.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    Optional<Score> saveScore(Score score);
    void deleteScore(Score score);
    List<Score> findAllScores();
    List<Score> findByUser(Long userId);
    List<Score> findByGame(Long gameId);
    List<Score> findByScorePoints(Long id);

}
