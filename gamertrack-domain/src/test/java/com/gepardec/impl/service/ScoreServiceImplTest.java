package com.gepardec.impl.service;

import com.gepardec.interfaces.repository.ScoreRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
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
        User user = new User("Max","Muster");
        user.setId(1L);
        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);
        Score score = new Score(user,game,10);

        when(scoreRepository.saveScore(score.getGame().getId(),
                score.getUser().getId(),10)).thenReturn(Optional.of(score));

        assertEquals(scoreService.saveScore(score.getGame().getId(),
                score.getUser().getId(),10).get().getScorePoints(), score.getScorePoints());
    }

    @Test
    void ensureSaveExistingScoreWorksCorrectly() {
        User user = new User("Max","Muster");
        user.setId(1L);
        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);
        Score score = new Score(user,game,10);

        when(scoreRepository.scoreExists(score.getGame().getId(),
                score.getUser().getId())).thenReturn(true);

        assertTrue(scoreService.scoreExists(score.getGame().getId(),
                score.getUser().getId()));
    }

    @Test
    void ensureUpdateExistingScoreWorksAndReturnsScore() {
        User user = new User("Max","Muster");
        user.setId(1L);
        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);
        Score scoreEdit = new Score(user,game,10);
        scoreEdit.setId(1L);

        //Score was found
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.of(scoreEdit));

        when(scoreRepository.saveScore(
                scoreEdit.getGame().getId(),scoreEdit.getUser().getId(), scoreEdit.getScorePoints()
        )).thenReturn(Optional.of(scoreEdit));

        Optional<Score> updatedScore = scoreService.updateScore(scoreEdit.getId(), scoreEdit);

        assertTrue(updatedScore.isPresent());
        assertEquals(scoreEdit.getUser().getId(), updatedScore.get().getUser().getId());
        assertEquals(scoreEdit.getGame().getId(), updatedScore.get().getGame().getId());
        assertEquals(scoreEdit.getScorePoints(), updatedScore.get().getScorePoints());
    }

    @Test
    void ensureUpdateNonExistingScoreWorksAndReturnsEmpty() {
        User user = new User("Max","Muster");
        user.setId(1L);
        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);
        Score scoreEdit = new Score(user,game,10);
        scoreEdit.setId(1L);

        //Score was found
        when(scoreRepository.findScoreById(scoreEdit.getId())).thenReturn(Optional.empty());

        Optional<Score> updatedScore = scoreService.updateScore(scoreEdit.getId(), scoreEdit);

        assertFalse(updatedScore.isPresent());
    }

    @Test
    void ensureFindAllScoresWorksAndReturnsAllScores() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user1,game,10);
        score2.setId(2L);

        Score score3 = new Score(user2,game,10);
        score3.setId(3L);

        when(scoreRepository.findAllScores()).thenReturn(List.of(score1,score2,score3));

        List<Score> foundScore = scoreService.findAllScores();

        assertFalse(foundScore.isEmpty());
        assertEquals(3, foundScore.size());
    }

    @Test
    void ensureFindScoreByIdWorksAndReturnsScore() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,10);
        score2.setId(2L);

        Score score3 = new Score(user3,game,10);
        score3.setId(3L);

        when(scoreRepository.findScoreById(2L)).thenReturn(Optional.of(score2));

        Optional<Score> foundScore = scoreService.findScoreById(2L);

        assertTrue(foundScore.isPresent());
        assertEquals(score2.getUser().getId(), foundScore.get().getUser().getId());
        assertEquals(score2.getGame().getId(), foundScore.get().getGame().getId());
        assertEquals(score2.getScorePoints(), foundScore.get().getScorePoints());
    }

    @Test
    void ensureFindScoreByUserWorksAndReturnsScore() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,10);
        score2.setId(2L);

        Score score3 = new Score(user3,game,10);
        score3.setId(3L);

        when(scoreRepository.findScoreByUser(2L)).thenReturn(List.of(score2));

        List<Score> foundScore = scoreService.findScoreByUser(2L);

        assertFalse(foundScore.isEmpty());
        assertEquals(score2.getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(score2.getGame().getId(), foundScore.getFirst().getGame().getId());
        assertEquals(score2.getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoreByGameWorksAndReturnsScore() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,10);
        score2.setId(2L);

        Score score3 = new Score(user3,game,10);
        score3.setId(3L);

        when(scoreRepository.findScoreByGame(1L)).thenReturn(List.of(score2));

        List<Score> foundScore = scoreService.findScoreByGame(1L);

        assertFalse(foundScore.isEmpty());
        assertEquals(score2.getUser().getId(), foundScore.getFirst().getUser().getId());
        assertEquals(score2.getGame().getId(), foundScore.getFirst().getGame().getId());
        assertEquals(score2.getScorePoints(), foundScore.getFirst().getScorePoints());
    }

    @Test
    void ensureFindScoreByScorePointsWorksAndReturnsScore() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,10);
        score2.setId(2L);

        Score score3 = new Score(user3,game,10);
        score3.setId(3L);

        when(scoreRepository.findScoreByScorePoints(10)).thenReturn(List.of(score1,score2,score3));

        List<Score> foundScore = scoreService.findScoreByScorePoints(10);

        assertFalse(foundScore.isEmpty());
        assertEquals(3, scoreService.findScoreByScorePoints(10).size());
    }

    @Test
    void ensureFindScoreByMinMaxPointsWorksAndReturnsScore() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,20);
        score2.setId(2L);

        Score score3 = new Score(user3,game,30);
        score3.setId(3L);

        when(scoreRepository.findScoreByMinMaxScorePoints(11,30)).thenReturn(List.of(score2,score3));

        List<Score> foundScore = scoreService.findScoreByMinMaxScorePoints(11,30);

        assertFalse(foundScore.isEmpty());
        assertEquals(2, scoreService.findScoreByMinMaxScorePoints(11,30).size());
    }

    @Test
    void ensureScoreExistsWorksAndReturnsFalse() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,20);
        score2.setId(2L);

        Score score3 = new Score(user3,game,30);
        score3.setId(3L);

        //Score does not exist yet
        when(scoreRepository.scoreExists(3L,1L)).thenReturn(false);

        assertFalse(scoreService.scoreExists(3L, 1L));
    }

    @Test
    void ensureScoreExistsWorksAndReturnsTrue() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("Sascha","Bayer");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Game game = new Game("Uno","nicht schummeln");
        game.setId(1L);

        Score score1 = new Score(user1,game,10);
        score1.setId(1L);

        Score score2 = new Score(user2,game,20);
        score2.setId(2L);

        Score score3 = new Score(user3,game,30);
        score3.setId(3L);

        //Score does not exist yet
        when(scoreRepository.scoreExists(3L,1L)).thenReturn(true);

        assertTrue(scoreService.scoreExists(3L, 1L));
    }
}
