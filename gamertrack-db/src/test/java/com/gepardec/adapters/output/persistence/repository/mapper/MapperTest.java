package com.gepardec.adapters.output.persistence.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.gepardec.TestFixtures;
import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

  @Mock
  EntityManager entityManager;

  @InjectMocks
  Mapper mapper = new Mapper();

  @Test
  public void ensureMapUserDtoToUserWorks() {
    User user = TestFixtures.user(1L);

    UserDto userDto = new UserDto(user);

    User mappedUser = mapper.toUser(userDto);

    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapUserDtoToExistingUserWorks() {
    User user = TestFixtures.user(1L);

    UserDto userDto = new UserDto(user);

    User mappedUser = mapper.toExistingUser(userDto, user);

    assertEquals(mappedUser.getId(), user.getId());
    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureMapScoreDtoToScoreWorks() {
    Score score = TestFixtures.score(1L, 1L, 1L);

    ScoreDto scoreDto = new ScoreDto(score);

    when(entityManager.getReference(User.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(Game.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    Score mappedScore = mapper.toScore(scoreDto);

    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }

  @Test
  public void ensureMapScoreDtoToExistingScoreWorks() {
    Score score = TestFixtures.score(1L, 1L, 1L);

    ScoreDto scoreDto = new ScoreDto(score);

    when(entityManager.getReference(User.class, scoreDto.userId())).thenReturn(
        TestFixtures.user(1L));
    when(entityManager.getReference(Game.class, scoreDto.gameId())).thenReturn(
        TestFixtures.game(1L));

    Score mappedScore = mapper.toExistingScore(scoreDto, score);

    assertEquals(mappedScore.getId(), scoreDto.id());
    assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
    assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
    assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
  }


  @Test
  void ensureGameOutcomeDtoToGameOutcomeWithReferenceWorks() {
    GameOutcomeDto gameOutcomeDto = new GameOutcomeDto(1L, 1L, List.of(1L));

    when(entityManager.getReference(Game.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(User.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    GameOutcome mappedGameOutcome = mapper.toGameOutcomeWithReference(gameOutcomeDto);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedGameOutcome.getId(), gameOutcomeDto.id());
    assertEquals(mappedGameOutcome.getGame().getId(), gameOutcomeDto.gameId());
    assertTrue(mappedGameOutcome.getUsers().stream().map(User::getId).toList()
        .containsAll(gameOutcomeDto.userIds()));

  }

  @Test
  void ensureGameOutcomeDtoToGameOutcomeWithReferenceWorksProvidingDtoAndEntity() {
    GameOutcomeDto gameOutcomeDto = new GameOutcomeDto(1L, 1L, List.of(1L));

    when(entityManager.getReference(Game.class, TestFixtures.game().getId())).thenReturn(
        TestFixtures.game());
    when(entityManager.getReference(User.class, TestFixtures.user(1L).getId())).thenReturn(
        TestFixtures.user(1L));

    GameOutcome mappedGameOutcome = mapper.toGameOutcomeWithReference(gameOutcomeDto,
        TestFixtures.gameOutcome(1L, TestFixtures.game(), List.of(TestFixtures.user(1L))));

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(mappedGameOutcome.getId(), gameOutcomeDto.id());
    assertEquals(mappedGameOutcome.getGame().getId(), gameOutcomeDto.gameId());
    assertTrue(mappedGameOutcome.getUsers().stream().map(User::getId).toList()
        .containsAll(gameOutcomeDto.userIds()));
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorks() {
    GameDto gameDto = TestFixtures.gameToGameDto(TestFixtures.game());

    Game mappedGame = mapper.toGame(gameDto);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(gameDto.title(), mappedGame.getName());
    assertEquals(gameDto.rules(), mappedGame.getRules());
  }

  @Test
  void ensureGameDtoToGameWithReferenceWorksProvidingDtoAndEntity() {
    GameDto gameDto = TestFixtures.gameToGameDto(TestFixtures.game());

    Game mappedGame = mapper.toGame(gameDto, TestFixtures.game());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(gameDto.id(), mappedGame.getId());
    assertEquals(gameDto.title(), mappedGame.getName());
    assertEquals(gameDto.rules(), mappedGame.getRules());
  }
}
