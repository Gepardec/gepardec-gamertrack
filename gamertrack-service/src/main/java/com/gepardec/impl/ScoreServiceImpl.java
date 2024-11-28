package com.gepardec.impl;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.interfaces.services.ScoreService;
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
    public Optional<Score> saveScore(Score score) {
        return scoreRepository.saveScore(score);
    }

    @Override
    public Optional<Score> updateScore(Long id, Score scoreEdit){
        Optional<Score> entity = scoreRepository.findById(id);
        if(entity.isPresent()) {
            Score score = entity.get();
            score.setUser(scoreEdit.getUser());
            score.setGame(scoreEdit.getGame());
            score.setScorePoints(scoreEdit.getScorePoints());
            return scoreRepository.saveScore(score);
        }
        return Optional.empty();
    }
}
