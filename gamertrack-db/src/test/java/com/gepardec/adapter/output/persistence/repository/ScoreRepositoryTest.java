package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.GameDto;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ArquillianExtension.class)
public class ScoreRepositoryTest extends GamertrackDbIT{
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    ScoreRepository scoreRepository;

    @Inject
    GameRepository gameRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    Mapper mapper;



    @Test
    void ensureSaveScoreWorks(){
        Game game = TestFixtures.game(1L);
        GameDto gameDto = TestFixtures.gameToGameDto(game);

        User user = TestFixtures.user(1L);
        UserDto userDto = new UserDto(user);


        Long savedGameId = gameRepository.saveGame(gameDto).get().getId();
        Long savedUserId = userRepository.saveUser(userDto).get().getId();

        Score score = new Score(user,game,10.0);

        ScoreDto scoreDto = new ScoreDto(1,savedUserId,savedGameId,10.0);

        Long savedId = scoreRepository.saveScore(scoreDto).get().getId();
        assertTrue(scoreRepository.findScoreById(savedId).isPresent());

    }
}
