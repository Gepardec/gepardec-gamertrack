package com.gepardec.adapter.output.persistence.repository.mapper;

import static com.gepardec.TestFixtures.game;
import static com.gepardec.TestFixtures.usersWithId;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchMapperTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private UserMapper userMapper;
  @Mock
  private GameMapper gameMapper;

  @InjectMocks
  private MatchMapper matchMapper;


  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorks() {
    Match match = new Match(1L, game(1L), usersWithId(3));

    when(gameMapper.gameModelToGameEntity(any())).thenReturn(
        new GameEntity(match.getGame().getId(), match.getGame().getName(),
            match.getGame().getRules()));

    MatchEntity mappedMatch = matchMapper.matchModelToMatchEntity(match);

    assertDoesNotThrow(() -> NullPointerException.class);
    //assertEquals(match.getId(), mappedMatch.getId());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    assertTrue(match.getUsers().stream().map(User::getId).toList()
        .containsAll(mappedMatch.getUsers().stream().map(UserEntity::getId).toList()));
  }

  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorksProvidingModelAndEntity() {
    Match match = new Match(1L, game(30L), usersWithId(3));

    when(gameMapper.gameModelToGameEntity(match.getGame())).thenReturn(
        new GameEntity(match.getGame().getId(), match.getGame().getName(),
            match.getGame().getRules()));

    MatchEntity matchEntity = matchMapper.matchModelToMatchEntity(match);
    matchEntity.setId(1L);
    when(entityManager.getReference(UserEntity.class, matchEntity.getId()))
        .thenReturn(matchEntity.getUsers().get(0))
        .thenReturn(matchEntity.getUsers().get(1))
        .thenReturn(matchEntity.getUsers().get(2));

    when(entityManager.getReference(GameEntity.class, matchEntity.getGame().getId())).thenReturn(
        matchEntity.getGame());

    MatchEntity mappedMatch = matchMapper.matchModelToMatchEntityWithReference(match, matchEntity);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(match.getId(), mappedMatch.getId());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    assertEquals(matchEntity.getUsers(), mappedMatch.getUsers());
  }


}
