package com.gepardec.impl.service;

import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Score;
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
    @Inject
    private TokenService tokenService;

    @Override
    public Optional<Score> saveScore(Score score) {

        if(!scoreExists(score)) {
            score.setToken(tokenService.generateToken());
            return scoreRepository.saveScore(score);
        }

        log.error("Score with userId: {} and gameId: {} already exists!", score.getUser().getId(), score.getGame().getId());
        return Optional.empty();
    }

    @Override
    public Optional<Score> updateScore(Score score) {
        Optional<Score> entity = scoreRepository.findScoreById(score.getId());
        if(entity.isPresent()) {

            log.info("Score with the id {} is present", score.getId());
            return scoreRepository.updateScore(score);
        }
        log.error("Could not find score with id {}. Score was not updated", score.getId());
        return Optional.empty();
    }

    @Override
    public List<Score> findAllScores(Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,null,null, includeDeactivatedUsers);
    }

    @Override
    public Optional<Score> findScoreById(Long id) {
        return scoreRepository.findScoreById(id);
    }

    @Override
    public List<Score> filterScores(Double minPoints, Double maxPoints, Long userId, Long gameId, Boolean includeDeactivatedUsers) {

        if(minPoints != null && maxPoints != null) {
            if (minPoints > maxPoints) {
                double tmp = maxPoints;
                maxPoints = minPoints;
                minPoints = tmp;
                log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
            }
        }

       return scoreRepository.filterScores(minPoints, maxPoints, userId, gameId,includeDeactivatedUsers);
    }



    @Override
    public List<Score> findScoresByUser(Long userId, Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,userId,null, includeDeactivatedUsers);
    }

    @Override
    public List<Score> findScoresByGame(Long gameId, Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,null,gameId, includeDeactivatedUsers);
    }

    @Override
    public List<Score> findTopScoresByGame(Long gameId, int top, Boolean includeDeactivatedUsers) {
        return scoreRepository.findTopScoreByGame(gameId,top,includeDeactivatedUsers);
    }

    @Override
    public List<Score> findScoreByScoresPoints(double scorePoints, Boolean includeDeactivatedUsers) {
        return scoreRepository.findScoreByScorePoints(scorePoints, includeDeactivatedUsers);
    }

    @Override
    public List<Score> findScoreByMinMaxScoresPoints(double minPoints, double maxPoints, Boolean includeDeactivatedUsers) {
        if(minPoints > maxPoints) {
            double tmp = maxPoints;
            maxPoints = minPoints;
            minPoints = tmp;
            log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
        }
        return scoreRepository.filterScores(minPoints,maxPoints,null,null,includeDeactivatedUsers);
    }

    @Override
    public boolean scoreExists(Score score) {
        return scoreRepository.scoreExists(score);
    }

}
