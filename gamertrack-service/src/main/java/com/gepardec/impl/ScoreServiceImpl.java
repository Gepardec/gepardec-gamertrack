package com.gepardec.impl;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.interfaces.services.GameService;
import com.gepardec.interfaces.services.ScoreService;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
@Stateless
public class ScoreServiceImpl implements ScoreService, Serializable {

    @Inject
    private ScoreRepository scoreRepository;

    //Only temporary for testing
    @Inject
    private UserService userService;
    //Only temporary for testing
    @Inject
    private GameService gameService;

    @Override
    public List<Score> findAllScores() {
        return scoreRepository.findAllScores();
    }

    @Override
    public Optional<Score> findById(Long id) {
        return scoreRepository.findById(id);
    }

    @Override
    public List<Score> findByUser(Long userId) {
        return scoreRepository.findByUser(userId);
    }

    @Override
    public List<Score> findByGame(Long gameId) {
        return scoreRepository.findByGame(gameId);
    }

    @Override
    public List<Score> findByScorePoints(double scorePoints) {
        return scoreRepository.findByScorePoints(scorePoints);
    }

    @Override
    public Optional<Score> saveScore(Long userId, Long gameId, double scorePoints) {
        return scoreRepository.saveScore(userId,gameId,scorePoints);
    }

    @Override
    public Optional<Score> updateScore(Long id, Score scoreEdit){
        Optional<Score> entity = scoreRepository.findById(id);
        if(entity.isPresent()) {
            Score score = entity.get();
            score.setUser(userService.findUserById(scoreEdit.user.getId()).get());
            score.setGame(gameService.findGameById(scoreEdit.game.getId()).get());
            score.setScorePoints(scoreEdit.getScorePoints());
            return scoreRepository.saveScore(
                    scoreEdit.user.getId(),scoreEdit.game.getId(),scoreEdit.getScorePoints());
        }
        return Optional.empty();
    }

}
