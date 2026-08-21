package com.gepardec.impl.service;

import com.gepardec.core.repository.ScoreHistoryRepository;
import com.gepardec.core.services.ScoreHistoryService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.ScoreHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
@ApplicationScoped
public class ScoreHistoryServiceImpl implements ScoreHistoryService, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ScoreHistoryServiceImpl.class);

    @Inject
    private ScoreHistoryRepository scoreHistoryRepository;
    @Inject
    private TokenService tokenService;

    @Override
    public Optional<ScoreHistory> saveScoreHistory(ScoreHistory scoreHistory) {
        scoreHistory.setToken(tokenService.generateToken());
        log.info(
            "Saving score history for userToken: {}, gameToken: {} and matchToken: {} with previousScorePoints: {}, newScorePoints: {} and scoreChange: {}",
            scoreHistory.getUser().getToken(), scoreHistory.getGame().getToken(),
            scoreHistory.getMatchToken(), scoreHistory.getPreviousScorePoints(),
            scoreHistory.getNewScorePoints(), scoreHistory.getScoreChange());
        return scoreHistoryRepository.saveScoreHistory(scoreHistory);
    }

    @Override
    public Optional<ScoreHistory> findScoreHistoryByToken(String token) {
        return scoreHistoryRepository.findScoreHistoryByToken(token);
    }

    @Override
    public List<ScoreHistory> filterScoreHistory(String userToken, String gameToken, String matchToken) {
        return scoreHistoryRepository.filterScoreHistory(userToken, gameToken, matchToken);
    }

    @Override
    public void deleteScoreHistoryByGame(String gameToken) {
        log.info("Deleting score history entries for gameToken: {}", gameToken);
        scoreHistoryRepository.deleteScoreHistoryByGame(gameToken);
    }
}
