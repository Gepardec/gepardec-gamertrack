package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.model.Score;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ScoreServiceImplTest {
    @Mock
    EntityManager entityManager;

    @Mock
    ScoreRepository scoreRepository;

    @InjectMocks
    ScoreServiceImpl scoreService;

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
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.of(scoreEdit));

        when(scoreRepository.updateScore(scoreEdit)).thenReturn(Optional.of(scoreEdit));

        Optional<Score> updatedScore = scoreService.updateScore(scoreEdit);

        assertTrue(updatedScore.isPresent());
        assertEquals(scoreEdit.getUser().getId(), updatedScore.get().getUser().getId());
        assertEquals(scoreEdit.getGame().getId(), updatedScore.get().getGame().getId());
        assertEquals(scoreEdit.getScorePoints(), updatedScore.get().getScorePoints());
    }

    @Test
    void ensureUpdateNonExistingScoreWorksAndReturnsEmpty() {
        Score scoreEdit = TestFixtures.score(1L,1L,1L);

        //Score was found
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.empty());

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

        when(scoreRepository.filterScores(10.0d,100.0d,1L,1L,true)).thenReturn(List.of(scores.get(0)));

        List<Score> foundScore = scoreService
                .filterScores(10D,100D,
                        1L,1L,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(1, foundScore.size());


    }
    @Test
    void ensureFindScoresFilterWorksAndReturnsFilteredByGameScores() {
        List<Score> scores = TestFixtures.scores(5);

        when(scoreRepository.filterScores(10.0d,100.0d,null,1L,true)).thenReturn(scores);

        List<Score> foundScore = scoreService
                .filterScores(10D,100D,null,1L,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(5, scoreService
                .filterScores(10D,100D, null,1L,true).size());


    }

    @Test
    void ensureFindScoreByIdWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreById(2L)).thenReturn(Optional.of(scores.get(1)));

        Optional<Score> foundScore = scoreService.findScoreById(2L);

        assertTrue(foundScore.isPresent());
        assertEquals(scores.get(1).getUser().getId(), foundScore.get().getUser().getId());
        assertEquals(scores.get(1).getGame().getId(), foundScore.get().getGame().getId());
        assertEquals(scores.get(1).getScorePoints(), foundScore.get().getScorePoints());
    }

    @Test
    void ensureFindScoresByUserWorksAndReturnsScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(null,null,2L,null,true)).thenReturn(List.of(scores.get(1)));

        List<Score> foundScore = scoreService.findScoresByUser(2L,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.get(1).getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(scores.get(1).getGame().getId(), foundScore.getFirst().getGame().getId());
        assertEquals(scores.get(1).getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoresByGameWorksAndReturnsScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.filterScores(null,null,null,1L,true)).thenReturn(List.of(scores.getFirst()));

        List<Score> foundScore = scoreService.findScoresByGame(1L,true);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.getFirst().getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(scores.getFirst().getGame().getId(), foundScore.getFirst().getGame().getId());
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
