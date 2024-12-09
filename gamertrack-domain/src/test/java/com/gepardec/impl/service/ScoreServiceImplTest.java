package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
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
import static org.mockito.ArgumentMatchers.eq;
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
        ScoreDto scoreDto = new ScoreDto(score);

        when(scoreRepository.saveScore(scoreDto)).thenReturn(Optional.of(score));

        assertEquals(scoreService.saveScore(scoreDto).get().getScorePoints(), score.getScorePoints());
    }

    @Test
    void ensureSaveExistingScoreWorksCorrectly() {

        Score score = TestFixtures.score(1L,1L,1L);
        ScoreDto scoreDto = new ScoreDto(score);

        when(scoreRepository.scoreExists(scoreDto)).thenReturn(true);

        assertTrue(scoreService.scoreExists(scoreDto));
    }

    @Test
    void ensureUpdateExistingScoreWorksAndReturnsScore() {
        Score scoreEdit = TestFixtures.score(1L,1L,1L);
        ScoreDto scoreDto = new ScoreDto(scoreEdit);

        //Score was found
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.of(scoreEdit));

        when(scoreRepository.updateScore(scoreDto)).thenReturn(Optional.of(scoreEdit));

        Optional<Score> updatedScore = scoreService.updateScore(scoreDto);

        assertTrue(updatedScore.isPresent());
        assertEquals(scoreEdit.getUser().getId(), updatedScore.get().getUser().getId());
        assertEquals(scoreEdit.getGame().getId(), updatedScore.get().getGame().getId());
        assertEquals(scoreEdit.getScorePoints(), updatedScore.get().getScorePoints());
    }

    @Test
    void ensureUpdateNonExistingScoreWorksAndReturnsEmpty() {
        Score scoreEdit = TestFixtures.score(1L,1L,1L);
        ScoreDto scoreDto = new ScoreDto(scoreEdit);

        //Score was found
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.empty());

        Optional<Score> updatedScore = scoreService.updateScore(scoreDto);

        assertFalse(updatedScore.isPresent());
    }

    @Test
    void ensureFindAllScoresWorksAndReturnsAllScores() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findAllScores()).thenReturn(scores);

        List<Score> foundScore = scoreService.findAllScores();

        assertFalse(foundScore.isEmpty());
        assertEquals(3, foundScore.size());
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
    void ensureFindScoreByUserWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByUser(2L)).thenReturn(List.of(scores.get(1)));

        List<Score> foundScore = scoreService.findScoreByUser(2L);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.get(1).getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(scores.get(1).getGame().getId(), foundScore.getFirst().getGame().getId());
        assertEquals(scores.get(1).getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoreByGameWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByGame(1L)).thenReturn(List.of(scores.getFirst()));

        List<Score> foundScore = scoreService.findScoreByGame(1L);

        assertFalse(foundScore.isEmpty());
        assertEquals(scores.getFirst().getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(scores.getFirst().getGame().getId(), foundScore.getFirst().getGame().getId());
        assertEquals(scores.getFirst().getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoreByScorePointsWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByScorePoints(10)).thenReturn(scores);

        List<Score> foundScore = scoreService.findScoreByScorePoints(10);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, scoreService.findScoreByScorePoints(10).size());
    }

    @Test
    void ensureFindScoreByMinMaxPointsWorksAndReturnsScore() {
        List<Score> scores = TestFixtures.scores(3);

        when(scoreRepository.findScoreByMinMaxScorePoints(11,30)).thenReturn(scores);

        List<Score> foundScore = scoreService.findScoreByMinMaxScorePoints(11,30);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, scoreService.findScoreByMinMaxScorePoints(11,30).size());
    }

    @Test
    void ensureScoreExistsWorksAndReturnsFalse() {
        List<Score> scores = TestFixtures.scores(3);

        ScoreDto scoreDto1 = new ScoreDto(scores.get(0));
        ScoreDto scoreDto2 = new ScoreDto(scores.get(1));
        ScoreDto scoreDto3 = new ScoreDto(scores.get(2));


        //Score does not exist yet
        when(scoreRepository.scoreExists(scoreDto3)).thenReturn(false);

        assertFalse(scoreService.scoreExists(scoreDto3));
    }

    @Test
    void ensureScoreExistsWorksAndReturnsTrue() {
        List<Score> scores = TestFixtures.scores(3);

        ScoreDto scoreDto1 = new ScoreDto(scores.get(0));
        ScoreDto scoreDto2 = new ScoreDto(scores.get(1));
        ScoreDto scoreDto3 = new ScoreDto(scores.get(2));

        //Score does not exist yet
        when(scoreRepository.scoreExists(scoreDto3)).thenReturn(true);

        assertTrue(scoreService.scoreExists(scoreDto3));
    }
}
