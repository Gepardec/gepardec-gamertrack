package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.ScoreMapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
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
public class ScoreRepositoryTest extends GamertrackDbIT {

  @PersistenceContext
  EntityManager entityManager;

  @Inject
  ScoreRepository scoreRepository;

  @Inject
  GameRepository gameRepository;

  @Inject
  UserRepository userRepository;

  @Inject
  TokenService tokenService;

  @Inject
  ScoreMapper scoreMapper;

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
    Game game = TestFixtures.game(null);

    User user = TestFixtures.user(1L);

    Score score = new Score(
        0L,
        userRepository.saveUser(user).get(),
        gameRepository.saveGame(game).get(),
        10, tokenService.generateToken());

    String savedToken = scoreRepository.saveScore(score).get().getToken();
    assertTrue(scoreRepository.findScoreByToken(savedToken).isPresent());

  }

  @Test
  void ensureUpdateScoreWorks() {

    User user = userRepository.saveUser(TestFixtures.user(1L)).get();
    Game game = gameRepository.saveGame(TestFixtures.game(null)).get();

    Score score = new Score(null, user, game, 10.0, tokenService.generateToken());

    Long savedId = scoreRepository.saveScore(score).get().getId();
    String savedToken = scoreRepository.saveScore(score).get().getToken();

    Score updatedScore = new Score(savedId, user, game, 20.0, tokenService.generateToken());

    scoreRepository.updateScore(updatedScore);

    Optional<Score> foundScore = scoreRepository.findScoreByToken(savedToken);
    assertTrue(foundScore.isPresent());
    assertEquals(foundScore.get().getScorePoints(), updatedScore.getScorePoints());
  }

  @Test
  void ensureFindAllScoresWorks() {

    User user1 = userRepository.saveUser(TestFixtures.user(1L)).get();
    User user2 = userRepository.saveUser(TestFixtures.user(2L)).get();
    User user3 = userRepository.saveUser(TestFixtures.user(3L)).get();

    Game game1 = gameRepository.saveGame(TestFixtures.game(null)).get();

    Score score1 = new Score(1L, user1, game1, 10.0, tokenService.generateToken());
    Score score2 = new Score(2L, user2, game1, 30.0, tokenService.generateToken());
    Score score3 = new Score(3L, user3, game1, 10.0, tokenService.generateToken());

    scoreRepository.saveScore(score1);
    scoreRepository.saveScore(score2);
    scoreRepository.saveScore(score3);

    List<Score> result = scoreRepository
        .filterScores(null, null, null, null, true);

    assertFalse(result.isEmpty());
    assertEquals(3, result.size());

  }

  @Test
  void ensureFindScoreByIdWorks() {
    User user1 = userRepository.saveUser(TestFixtures.user(1L)).get();
    User user2 = userRepository.saveUser(TestFixtures.user(2L)).get();

    Game game1 = gameRepository.saveGame(TestFixtures.game(null)).get();

    Score score1 = new Score(1L, user1, game1, 10.0, tokenService.generateToken());
    Score score2 = new Score(2L, user2, game1, 30.0, tokenService.generateToken());

    String savedToken = scoreRepository.saveScore(score1).get().getToken();
    scoreRepository.saveScore(score2);

    assertEquals(score1.getScorePoints(),
        scoreRepository.findScoreByToken(savedToken).get().getScorePoints());
  }

  @Test
  void ensureFindTopScoresByGameWorks() {
    User user1 = userRepository.saveUser(TestFixtures.user(1L)).get();
    User user2 = userRepository.saveUser(TestFixtures.user(2L)).get();
    User user3 = userRepository.saveUser(TestFixtures.user(3L)).get();

    Game game1 = gameRepository.saveGame(TestFixtures.game(null)).get();

    Score score1 = new Score(1L, user1, game1, 10.0, tokenService.generateToken());
    Score score2 = new Score(2L, user2, game1, 30.0, tokenService.generateToken());
    Score score3 = new Score(3L, user3, game1, 10.0, tokenService.generateToken());

    scoreRepository.saveScore(score1);
    scoreRepository.saveScore(score2);
    scoreRepository.saveScore(score3);

    String savedToken = game1.getToken();

    assertEquals(3, scoreRepository.filterScores(null, null, null, null, true).size());
    assertFalse(scoreRepository.findTopScoreByGame(savedToken, 2, true).isEmpty());
    assertEquals(2, scoreRepository.findTopScoreByGame(savedToken, 2, true).size());
  }

}
