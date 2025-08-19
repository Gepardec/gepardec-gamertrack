package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Score;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;



public class ScoreServiceImplTest {
    @Mock
    EntityManager entityManager;

    @Mock
    ScoreRepository scoreRepository;

    @InjectMocks
    ScoreServiceImpl scoreService;

    @Mock
    TokenService tokenService;

    @Test
    void ensureSaveAndReadScoreWorks() {
        Score score = TestFixtures.score(1L,1L,1L);

        when(scoreRepository.saveScore(score)).thenReturn(Optional.of(score));

        assertEquals(scoreService.saveScore(score).get().getScorePoints(), score.getScorePoints());
    }

    @Test
    void ensureSaveExistingScoreWorksCorrectly() {

        Score score = TestFixtures.score(1L,1L,1L);

        when(scoreRepository.scoreExists(score)).thenReturn(true);

        assertTrue(scoreService.scoreExists(score));
    }

    @Test
    void ensureUpdateExistingScoreWorksAndReturnsScore() {
        Score scoreEdit = TestFixtures.score(1L,1L,1L);

        //Score was found
        when(scoreRepository.findScoreByToken(scoreEdit.getToken())).thenReturn(Optional.of(scoreEdit));

        when(scoreRepository.updateScore(scoreEdit)).thenReturn(Optional.of(scoreEdit));

        Optional<Score> updatedScore = scoreService.updateScore(scoreEdit);

        assertTrue(updatedScore.isPresent());
        assertEquals(scoreEdit.getUser().getToken(), updatedScore.get().getUser().getToken());
        assertEquals(scoreEdit.getGame().getToken(), updatedScore.get().getGame().getToken());
        assertEquals(scoreEdit.getScorePoints(), updatedScore.get().getScorePoints());
    }

    @Test
    void ensureUpdateNonExistingScoreWorksAndReturnsEmpty() {
        Score scoreEdit = TestFixtures.score(1L,1L,1L);

        //Score was found
        when(scoreRepository.findScoreByToken(scoreEdit.getToken())).thenReturn(Optional.empty());

        Optional<Score> updatedScore = scoreService.updateScore(scoreEdit);

        assertFalse(updatedScore.isPresent());
    }

    @Test
    void ensureFindAllScoresWorksAndReturnsAllScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(null,null,null,null,true)).thenReturn(scores);

        List<Score> foundScore = scoreService.findAllScores(true);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, foundScore.size());
    }
    @Test
    void ensureFindScoresFilterWorksAndReturnsFilteredByUserScores() {
        List<Score> scores = TestFixtures.scores(5);

        when(scoreRepository.filterScores(10.0d,100.0d,scores.get(0).getUser().getToken(),scores.get(0).getGame().getToken(),true)).thenReturn(List.of(scores.get(0)));

        List<Score> foundScore = scoreService
                .filterScores(10D,100D,
                        scores.get(0).getUser().getToken(),scores.get(0).getGame().getToken(),true);

        assertFalse(foundScore.isEmpty());
        assertEquals(1, foundScore.size());


    }
    @Test
    void ensureFindScoresFilterWorksAndReturnsFilteredByGameScores() {
        List<Score> scores = TestFixtures.scores(5);

        when(scoreRepository.filterScores(10.0d,100.0d,null,scores.get(0).getGame().getToken(),true)).thenReturn(scores);

        List<Score> foundScore = scoreService
                .filterScores(10D,100D,null,scores.get(0).getGame().getToken(),true);

        assertFalse(foundScore.isEmpty());
        assertEquals(5, scoreService
                .filterScores(10D,100D, null,scores.get(0).getGame().getToken(),true).size());

    }

    @Test
    void ensureFindScoreByTokenWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByToken(scores.get(1).getToken())).thenReturn(Optional.of(scores.get(1)));

        Optional<Score> foundScore = scoreService.findScoreByToken(scores.get(1).getToken());

        assertTrue(foundScore.isPresent());
        assertEquals(scores.get(1).getUser().getToken(), foundScore.get().getUser().getToken());
        assertEquals(scores.get(1).getGame().getToken(), foundScore.get().getGame().getToken());
        assertEquals(scores.get(1).getScorePoints(), foundScore.get().getScorePoints());
    }

    @Test
    void ensureFindScoresByUserWorksAndReturnsScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(null,null,scores.get(1).getUser().getToken(),null,true)).thenReturn(List.of(scores.get(1)));

        List<Score> foundScore = scoreService.findScoresByUser(scores.get(1).getUser().getToken(),true);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.get(1).getUser().getToken(), foundScore.getFirst().getUser().getToken());
        assertEquals(scores.get(1).getGame().getToken(), foundScore.getFirst().getGame().getToken());
        assertEquals(scores.get(1).getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoresByGameWorksAndReturnsScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(null,null,null,scores.getFirst().getGame().getToken(),true)).thenReturn(List.of(scores.getFirst()));

        List<Score> foundScore = scoreService.findScoresByGame(scores.getFirst().getGame().getToken(),true);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.getFirst().getUser().getToken(), foundScore.getFirst().getUser().getToken());
        assertEquals(scores.getFirst().getGame().getToken(), foundScore.getFirst().getGame().getToken());
        assertEquals(scores.getFirst().getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoresByScorePointsWorksAndReturnsScoreByScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByScorePoints(10,true)).thenReturn(scores);

        List<Score> foundScore = scoreService.findScoreByScoresPoints(10,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, scoreService.findScoreByScoresPoints(10,true).size());
    }

    @Test
    void ensureFindScoresByMinMaxPointsWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(11D,30D,null,null,true)).thenReturn(scores);

        List<Score> foundScore = scoreService.findScoreByMinMaxScoresPoints(11,30,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, scoreService.findScoreByMinMaxScoresPoints(11,30,true).size());
    }

    @Test
    void ensureScoreExistsWorksAndReturnsFalse() {
        List<Score> scores = TestFixtures.scores(3);

        //Score does not exist yet
        when(scoreRepository.scoreExists(scores.get(2))).thenReturn(false);

        assertFalse(scoreService.scoreExists(scores.get(2)));
    }

    @Test
    void ensureScoreExistsWorksAndReturnsTrue() {
        List<Score> scores = TestFixtures.scores(3);

        //Score does not exist yet
        when(scoreRepository.scoreExists(scores.get(2))).thenReturn(true);

        assertTrue(scoreService.scoreExists(scores.get(2)));
    }
}
