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
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Inject
    private UserTransaction utx;


    @BeforeEach
    void beforeEach() throws Exception {
        super.removeTableData(entityManager, utx, Score.class);
        super.removeTableData(entityManager, utx, Game.class);
        super.removeTableData(entityManager, utx, User.class);
    }

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

    @Test
    void ensureUpdateScoreWorks(){
        Game game = TestFixtures.game(1L);
        GameDto gameDto = TestFixtures.gameToGameDto(game);

        User user = TestFixtures.user(1L);
        UserDto userDto = new UserDto(user);

        Long savedGameId = gameRepository.saveGame(gameDto).get().getId();
        Long savedUserId = userRepository.saveUser(userDto).get().getId();

        Score score = new Score(user,game,10.0);
        ScoreDto scoreDto = new ScoreDto(1,savedUserId,savedGameId,10.0);

        Long savedId = scoreRepository.saveScore(scoreDto).get().getId();

        ScoreDto updatedScoreDto = new ScoreDto(savedId,savedUserId,savedGameId,20.0);

        scoreRepository.updateScore(updatedScoreDto);

        Optional<Score> foundScore = scoreRepository.findScoreById(savedId);
        assertTrue(foundScore.isPresent());
        assertEquals(foundScore.get().getScorePoints(),updatedScoreDto.scorePoints());
    }

    @Test
    void ensureFindAllScoresWorks(){
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        UserDto userDto3 = new UserDto(user3);

        Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
        Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
        Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

        Game game1 = TestFixtures.game();

        GameDto gameDto1 = TestFixtures.gameToGameDto(game1);

        Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

        Score score1 = new Score(user1,game1,10.0);
        Score score2 = new Score(user2,game1,30.0);
        Score score3 = new Score(user3,game1,10.0);

        ScoreDto scoreDto1 = new ScoreDto(1L,savedUserId1,savedGameId1,10.0);
        ScoreDto scoreDto2 = new ScoreDto(2L,savedUserId2,savedGameId1,30.0);
        ScoreDto scoreDto3 = new ScoreDto(3L,savedUserId3,savedGameId1,10.0);

        scoreRepository.saveScore(scoreDto1).get().getId();
        scoreRepository.saveScore(scoreDto2).get().getId();
        scoreRepository.saveScore(scoreDto3).get().getId();

        assertFalse(scoreRepository.findAllScores().isEmpty());
        assertEquals(3, scoreRepository.findAllScores().size());

    }

    @Test
    void ensureFindScoreByIdWorks(){
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);

        Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
        Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();

        Game game1 = TestFixtures.game();
        Game game2 = TestFixtures.game();

        GameDto gameDto1 = TestFixtures.gameToGameDto(game1);
        GameDto gameDto2 = TestFixtures.gameToGameDto(game2);

        Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();
        Long savedGameId2 = gameRepository.saveGame(gameDto2).get().getId();

        Score score1 = new Score(user1,game1,10.0);
        Score score2 = new Score(user2,game2,10.0);

        ScoreDto scoreDto1 = new ScoreDto(1L,savedUserId1,savedGameId1,10.0);
        ScoreDto scoreDto2 = new ScoreDto(2L,savedUserId2,savedGameId2,30.0);

        Long savedId1 = scoreRepository.saveScore(scoreDto1).get().getId();
        scoreRepository.saveScore(scoreDto2);

        assertFalse(scoreRepository.findAllScores().isEmpty());
        assertEquals(scoreDto1.scorePoints(), scoreRepository.findScoreById(savedId1).get().getScorePoints());
    }

    @Test
    void ensureFindTopScoresByGameWorks(){
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        UserDto userDto3 = new UserDto(user3);

        Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
        Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
        Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

        Game game1 = TestFixtures.game();

        GameDto gameDto1 = TestFixtures.gameToGameDto(game1);

        Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

        Score score1 = new Score(user1,game1,10.0);
        Score score2 = new Score(user2,game1,30.0);
        Score score3 = new Score(user3,game1,10.0);

        ScoreDto scoreDto1 = new ScoreDto(1L,savedUserId1,savedGameId1,10.0);
        ScoreDto scoreDto2 = new ScoreDto(2L,savedUserId2,savedGameId1,30.0);
        ScoreDto scoreDto3 = new ScoreDto(3L,savedUserId3,savedGameId1,10.0);

        scoreRepository.saveScore(scoreDto1);
        scoreRepository.saveScore(scoreDto2);
        scoreRepository.saveScore(scoreDto3);


        assertEquals(3, scoreRepository.findAllScores().size());
        assertFalse(scoreRepository.findTopScoreByGame(savedGameId1,2).isEmpty());
        assertEquals(2, scoreRepository.findTopScoreByGame(savedGameId1,2).size());
    }

    @Test
    void ensureFindTopScoresByUserWorks(){
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        UserDto userDto3 = new UserDto(user3);

        Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
        Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
        Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

        Game game1 = TestFixtures.game();

        GameDto gameDto1 = TestFixtures.gameToGameDto(game1);

        Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

        Score score1 = new Score(user1,game1,10.0);
        Score score2 = new Score(user2,game1,30.0);
        Score score3 = new Score(user3,game1,10.0);

        ScoreDto scoreDto1 = new ScoreDto(1L,savedUserId1,savedGameId1,10.0);
        ScoreDto scoreDto2 = new ScoreDto(2L,savedUserId2,savedGameId1,30.0);
        ScoreDto scoreDto3 = new ScoreDto(3L,savedUserId3,savedGameId1,10.0);

        Long savedId1 = scoreRepository.saveScore(scoreDto1).get().getId();
        Long savedId2 = scoreRepository.saveScore(scoreDto2).get().getId();
        Long savedId3 = scoreRepository.saveScore(scoreDto3).get().getId();

        assertTrue(userRepository.existsByUserId(List.of(savedId1,savedId2,savedId3)));
        assertFalse(userRepository.existsByUserId(List.of(1000L,1001L)));

    }
}
