package com.gepardec.impl.service;

import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.services.ScoreService;
import com.gepardec.model.Score;
import com.gepardec.model.dto.ScoreDto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Stateless
public class ScoreServiceImpl implements ScoreService, Serializable {

    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);
    @Inject
    private ScoreRepository scoreRepository;

    @Override
    public Optional<Score> saveScore(ScoreDto scoreDto) {
        if(!scoreExists(scoreDto)) {
            return scoreRepository.saveScore(scoreDto);
        }

        log.error("Score with userId: {} and gameId: {} already exists!", scoreDto.userId(), scoreDto.gameId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> updateScore(ScoreDto scoreDto) {
        Optional<Score> entity = scoreRepository.findScoreById(scoreDto.id());
        if(entity.isPresent()) {

            log.info("Score with the id {} is present", scoreDto.id());
            return scoreRepository.updateScore(scoreDto);
        }
        log.error("Could not find score with id {}. Score was not updated", scoreDto.id());
        return Optional.empty();
    }

    @Override
    public List<Score> findAllScores() {
        return scoreRepository.findAllScores();
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        return scoreRepository.findScoreById(id);
    }

    @Override
    public List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId) {

        if(minPoints != null && maxPoints != null) {
            if (minPoints > maxPoints) {
                double tmp = maxPoints;
                maxPoints = minPoints;
                minPoints = tmp;
                log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
            }
        }

       return scoreRepository.filterScores(minPoints, maxPoints, userId, gameId);
    }



    @Override
    public List<Score> findScoresByUser(Long userId) {
        return scoreRepository.filterScores(null,null,userId,null);
    }

    @Override
    public List<Score> findScoresByGame(Long gameId) {
        return scoreRepository.filterScores(null,null,null,gameId);
    }

    @Override
    public List<Score> findTopScoresByGame(Long gameId, int top) {
        return scoreRepository.findTopScoreByGame(gameId,top);
    }

    @Override
    public List<Score> findScoreByScoresPoints(double scorePoints) {
        return scoreRepository.findScoreByScorePoints(scorePoints);
    }

    @Override
    public List<Score> findScoreByMinMaxScoresPoints(double minPoints, double maxPoints) {
        if(minPoints > maxPoints) {
            double tmp = maxPoints;
            maxPoints = minPoints;
            minPoints = tmp;
            log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
        }
        return scoreRepository.filterScores(minPoints,maxPoints,null,null);
    }

    @Override
    public boolean scoreExists(ScoreDto scoreDto) {
        return scoreRepository.scoreExists(scoreDto);
    }

}
