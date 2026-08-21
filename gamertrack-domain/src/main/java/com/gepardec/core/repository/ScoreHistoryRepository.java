package com.gepardec.core.repository;

import com.gepardec.model.ScoreHistory;

import java.util.List;
import java.util.Optional;

public interface ScoreHistoryRepository {
    Optional<ScoreHistory> saveScoreHistory(ScoreHistory scoreHistory);
    Optional<ScoreHistory> findScoreHistoryByToken(String token);
    List<ScoreHistory> filterScoreHistory(String userToken, String gameToken, String matchToken);
    void deleteScoreHistoryByGame(String gameToken);
}
