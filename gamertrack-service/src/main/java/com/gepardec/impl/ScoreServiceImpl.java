package com.gepardec.impl;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.interfaces.services.ScoreService;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.Score;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
@Stateless
public class ScoreServiceImpl implements ScoreService, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);
    @Inject
    private ScoreRepository scoreRepository;

    @Override
    public List<Score> findAllScores() {
        return scoreRepository.findAllScores();
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        return scoreRepository.findScoreById(id);
    }

    @Override
    public List<Score> findScoreByUser(Long userId) {
        return scoreRepository.findScoreByUser(userId);
    }

    @Override
    public List<Score> findScoreByGame(Long gameId) {
        return scoreRepository.findScoreByGame(gameId);
    }

    @Override
    public List<Score> findScoreByScorePoints(double scorePoints) {
        return scoreRepository.findScoreByScorePoints(scorePoints);
    }

    @Override
    public List<Score> findScoreByMinMaxScorePoints(double minPoints, double maxPoints) {
        if(minPoints > maxPoints) {
            double tmp = maxPoints;
            maxPoints = minPoints;
            minPoints = tmp;
            log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
        }
        return scoreRepository.findScoreByMinMaxScorePoints(minPoints, maxPoints);
    }

    @Override
    public Optional<Score> saveScore(Long userId, Long gameId, double scorePoints) {
        return scoreRepository.saveScore(userId,gameId,scorePoints);
    }

    @Override
    public Optional<Score> updateScore(Long id, Score scoreEdit){
        Optional<Score> entity = scoreRepository.findScoreById(id);
        if(entity.isPresent()) {
            log.info("Score with the id {} is present", id);
            return scoreRepository.saveScore(
                    scoreEdit.user.getId(),scoreEdit.game.getId(),scoreEdit.getScorePoints());
        }
        log.error("Could not find score with id {}. Score was not updated", id);
        return Optional.empty();
    }

    @Override
    public boolean scoreExists(Long userId, Long gameId) {
        return scoreRepository.scoreExists(userId,gameId);
    }

}
