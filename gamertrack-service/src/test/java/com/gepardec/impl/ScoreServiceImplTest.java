package com.gepardec.impl;

import com.gepardec.adapters.output.persistence.repository.ScoreRepositoryImpl;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ScoreServiceImplTest {
    @Mock
    EntityManager entityManager;

    @Mock
    ScoreRepositoryImpl scoreRepository;

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
}
