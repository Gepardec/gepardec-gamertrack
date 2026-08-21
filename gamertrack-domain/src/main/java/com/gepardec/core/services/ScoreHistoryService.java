package com.gepardec.core.services;

import com.gepardec.model.ScoreHistory;

import java.util.List;
import java.util.Optional;

public interface ScoreHistoryService {
    Optional<ScoreHistory> saveScoreHistory(ScoreHistory scoreHistory);
    Optional<ScoreHistory> findScoreHistoryByToken(String token);
    List<ScoreHistory> filterScoreHistory(String userToken, String gameToken, String matchToken);
    void deleteScoreHistoryByGame(String gameToken);
}
