package com.gepardec.adapter.output.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.EntityMapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.util.List;
import java.util.Optional;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class ScoreEntityRepositoryTest extends GamertrackDbIT {

  @PersistenceContext
  EntityManager entityManager;

  @Inject
  ScoreRepository scoreRepository;

  @Inject
  GameRepository gameRepository;

  @Inject
  UserRepository userRepository;

  @Inject
  EntityMapper entityMapper;

  @Inject
  private UserTransaction utx;


  @BeforeEach
  void beforeEach() throws Exception {
    super.removeTableData(ScoreEntity.class);
    super.removeTableData(GameEntity.class);
    super.removeTableData(UserEntity.class);
  }

  @Test
  void ensureSaveScoreWorks() {
    GameEntity game = TestFixtures.game(1L);
    Game gameDto = TestFixtures.gameToGameDto(game);

    UserEntity user = TestFixtures.user(1L);
    User userDto = new User(user);

    Long savedGameId = gameRepository.saveGame(gameDto).get().getId();
    Long savedUserId = userRepository.saveUser(userDto).get().getId();

    Score score = new Score(1, savedUserId, savedGameId, 10.0);

    Long savedId = scoreRepository.saveScore(score).get().getId();
    assertTrue(scoreRepository.findScoreById(savedId).isPresent());

  }

  @Test
  void ensureUpdateScoreWorks() {
    GameEntity game = TestFixtures.game(1L);
    Game gameDto = TestFixtures.gameToGameDto(game);

    UserEntity user = TestFixtures.user(1L);
    User userDto = new User(user);

    Long savedGameId = gameRepository.saveGame(gameDto).get().getId();
    Long savedUserId = userRepository.saveUser(userDto).get().getId();

    Score score = new Score(1, savedUserId, savedGameId, 10.0);

    Long savedId = scoreRepository.saveScore(score).get().getId();

    Score updatedScore = new Score(savedId, savedUserId, savedGameId, 20.0);

    scoreRepository.updateScore(updatedScore);

    Optional<ScoreEntity> foundScore = scoreRepository.findScoreById(savedId);
    assertTrue(foundScore.isPresent());
    assertEquals(foundScore.get().getScorePoints(), updatedScore.scorePoints());
  }

  @Test
  void ensureFindAllScoresWorks() {
    UserEntity user1 = TestFixtures.user(1L);
    UserEntity user2 = TestFixtures.user(2L);
    UserEntity user3 = TestFixtures.user(3L);

    User userDto1 = new User(user1);
    User userDto2 = new User(user2);
    User userDto3 = new User(user3);

    Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
    Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
    Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

    GameEntity game1 = TestFixtures.game();

    Game gameDto1 = TestFixtures.gameToGameDto(game1);

    Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

    Score score1 = new Score(1L, savedUserId1, savedGameId1, 10.0);
    Score score2 = new Score(2L, savedUserId2, savedGameId1, 30.0);
    Score score3 = new Score(3L, savedUserId3, savedGameId1, 10.0);

    scoreRepository.saveScore(score1).get().getId();
    scoreRepository.saveScore(score2).get().getId();
    scoreRepository.saveScore(score3).get().getId();

    assertFalse(scoreRepository.findAllScores().isEmpty());
    assertEquals(3, scoreRepository.findAllScores().size());

  }

  @Test
  void ensureFindScoreByIdWorks() {
    UserEntity user1 = TestFixtures.user(1L);
    UserEntity user2 = TestFixtures.user(2L);

    User userDto1 = new User(user1);
    User userDto2 = new User(user2);

    Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
    Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();

    GameEntity game1 = TestFixtures.game();
    GameEntity game2 = TestFixtures.game();

    Game gameDto1 = TestFixtures.gameToGameDto(game1);
    Game gameDto2 = TestFixtures.gameToGameDto(game2);

    Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();
    Long savedGameId2 = gameRepository.saveGame(gameDto2).get().getId();

    Score score1 = new Score(1L, savedUserId1, savedGameId1, 10.0);
    Score score2 = new Score(2L, savedUserId2, savedGameId2, 30.0);

    Long savedId1 = scoreRepository.saveScore(score1).get().getId();
    scoreRepository.saveScore(score2);

    assertFalse(scoreRepository.findAllScores().isEmpty());
    assertEquals(score1.scorePoints(),
        scoreRepository.findScoreById(savedId1).get().getScorePoints());
  }

  @Test
  void ensureFindTopScoresByGameWorks() {
    UserEntity user1 = TestFixtures.user(1L);
    UserEntity user2 = TestFixtures.user(2L);
    UserEntity user3 = TestFixtures.user(3L);

    User userDto1 = new User(user1);
    User userDto2 = new User(user2);
    User userDto3 = new User(user3);

    Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
    Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
    Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

    GameEntity game1 = TestFixtures.game();

    Game gameDto1 = TestFixtures.gameToGameDto(game1);

    Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

    Score score1 = new Score(1L, savedUserId1, savedGameId1, 10.0);
    Score score2 = new Score(2L, savedUserId2, savedGameId1, 30.0);
    Score score3 = new Score(3L, savedUserId3, savedGameId1, 10.0);

    scoreRepository.saveScore(score1);
    scoreRepository.saveScore(score2);
    scoreRepository.saveScore(score3);

    assertEquals(3, scoreRepository.findAllScores().size());
    assertFalse(scoreRepository.findTopScoreByGame(savedGameId1, 2).isEmpty());
    assertEquals(2, scoreRepository.findTopScoreByGame(savedGameId1, 2).size());
  }

  @Test
  void ensureFindTopScoresByUserWorks() {
    UserEntity user1 = TestFixtures.user(1L);
    UserEntity user2 = TestFixtures.user(2L);
    UserEntity user3 = TestFixtures.user(3L);

    User userDto1 = new User(user1);
    User userDto2 = new User(user2);
    User userDto3 = new User(user3);

    Long savedUserId1 = userRepository.saveUser(userDto1).get().getId();
    Long savedUserId2 = userRepository.saveUser(userDto2).get().getId();
    Long savedUserId3 = userRepository.saveUser(userDto3).get().getId();

    GameEntity game1 = TestFixtures.game();

    Game gameDto1 = TestFixtures.gameToGameDto(game1);

    Long savedGameId1 = gameRepository.saveGame(gameDto1).get().getId();

    Score score1 = new Score(1L, savedUserId1, savedGameId1, 10.0);
    Score score2 = new Score(2L, savedUserId2, savedGameId1, 30.0);
    Score score3 = new Score(3L, savedUserId3, savedGameId1, 10.0);

    Long savedId1 = scoreRepository.saveScore(score1).get().getId();
    Long savedId2 = scoreRepository.saveScore(score2).get().getId();
    Long savedId3 = scoreRepository.saveScore(score3).get().getId();

    assertTrue(userRepository.existsByUserId(List.of(savedId1, savedId2, savedId3)));
    assertFalse(userRepository.existsByUserId(List.of(1000L, 1001L)));

  }
}
