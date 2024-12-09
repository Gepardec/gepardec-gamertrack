package com.gepardec.interfaces.repository;

import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    List<Score> findAllScores();
    Optional<Score> findScoreById(Long id);
    List<Score> findScoreByUser(Long userId);
    List<Score> findScoreByGame(Long gameId);
    List<Score> findScoreByScorePoints(double scorePoints);
    List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints);
    Optional<Score> saveScore(ScoreDto scoreDto);
    Optional<Score> updateScore(ScoreDto scoreDto);
    boolean scoreExists(ScoreDto scoreDto);
}
