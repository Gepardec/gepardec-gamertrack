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

        log.error("Score with userToken: {} and gameToken: {} already exists!", score.getUser().getToken(), score.getGame().getToken());
        return Optional.empty();
    }

    @Override
    public Optional<Score> updateScore(Score score) {
        Optional<Score> entity = scoreRepository.findScoreByToken(score.getToken());
        if(entity.isPresent()) {

            log.info("Score with the token {} is present", score.getToken());
            return scoreRepository.updateScore(score);
        }
        log.error("Could not find score with token {}. Score was not updated", score.getToken());
        return Optional.empty();
    }

    @Override
    public void deleteScore(String token) {
        Optional<Score> score = scoreRepository.findScoreByToken(token);

        if(score.isPresent()){
            log.info("deleting: score with the token {} user: {} game: {}", score.get().getToken(),score.get().getUser().getToken(),score.get().getGame().getToken());
                scoreRepository.deleteScore(score.get());
            }
        log.error("Could not find score with token {}. Score was not deleted", token);
    }

    @Override
    public List<Score> findAllScores(Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,null,null, includeDeactivatedUsers);
    }

    @Override
    public Optional<Score> findScoreByToken(String token) {
        return scoreRepository.findScoreByToken(token);
    }

    @Override
    public List<Score> filterScores(Double minPoints, Double maxPoints, String userToken, String gameToken, Boolean includeDeactivatedUsers) {

        if(minPoints != null && maxPoints != null) {
            if (minPoints > maxPoints) {
                double tmp = maxPoints;
                maxPoints = minPoints;
                minPoints = tmp;
                log.info("switched minPoints with maxPoint because minPoints was greater than maxPoints");
            }
        }

       return scoreRepository.filterScores(minPoints, maxPoints, userToken, gameToken,includeDeactivatedUsers);
    }



    @Override
    public List<Score> findScoresByUser(String userToken, Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,userToken,null, includeDeactivatedUsers);
    }

    @Override
    public List<Score> findScoresByGame(String gameToken, Boolean includeDeactivatedUsers) {
        return scoreRepository.filterScores(null,null,null,gameToken, includeDeactivatedUsers);
    }

    @Override
    public List<Score> findTopScoresByGame(String gameToken, int top, Boolean includeDeactivatedUsers) {
        return scoreRepository.findTopScoreByGame(gameToken,top,includeDeactivatedUsers);
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
