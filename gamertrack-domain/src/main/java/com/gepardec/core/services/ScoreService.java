package com.gepardec.core.services;

import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;

import java.util.List;
import java.util.Optional;

public interface ScoreService {
    List<Score> findAllScores(Boolean includeDeactivated);
    Optional<Score> findScoreById(Long id);
    List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId, Boolean includeDeactivatedUsers);
    List<Score> findScoresByUser(Long userId, Boolean includeDeactivatedUsers);
    List<Score> findScoresByGame(Long gameId, Boolean includeDeactivatedUsers);
    List<Score> findTopScoresByGame(Long gameId, int top, Boolean includeDeactivatedUsers);
    List<Score> findScoreByScoresPoints(double scorePoints, Boolean includeDeactivatedUsers);
    List<Score> findScoreByMinMaxScoresPoints(double minPoints, double maxPoints, Boolean includeDeactivatedUsers);
    Optional<Score> saveScore(ScoreDto scoreDto);
    Optional<Score> updateScore(ScoreDto scoreDto);
    boolean scoreExists(ScoreDto scoreDto);
}
