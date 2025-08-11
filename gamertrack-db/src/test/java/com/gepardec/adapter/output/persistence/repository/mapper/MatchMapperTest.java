package com.gepardec.adapter.output.persistence.repository.mapper;

import static com.gepardec.TestFixtures.game;
import static com.gepardec.TestFixtures.match;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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


public class MatchMapperTest {

  @Mock
  EntityManager entityManager;

  @Mock
  GameMapper gameMapper = new GameMapper();

  @InjectMocks
  MatchMapper matchMapper = new MatchMapper();


  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorks() {
    Match match = match();

    when(gameMapper.gameModelToGameEntity(match.getGame())).thenReturn(
        new GameEntity(match.getGame().getId(), game().getToken(), match.getGame().getName(),
            match.getGame().getRules()));
    MatchEntity mappedMatch = matchMapper.matchModelToMatchEntity(match);

    assertDoesNotThrow(() -> NullPointerException.class);
    assertEquals(match.getToken(), mappedMatch.getToken());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    assertTrue(match.getUsers().stream().map(User::getId).toList()
        .containsAll(mappedMatch.getUsers().stream().map(UserEntity::getId).toList()));
  }

  @Test
  void ensureMatchModelToMatchWithReferenceEntityMappingWorksProvidingModelAndEntity() {
    Match match = match();

    when(gameMapper.gameModelToGameEntity(match.getGame())).thenReturn(
        new GameEntity(match.getGame().getId(), game().getToken(), match.getGame().getName(),
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
    assertEquals(match.getToken(), mappedMatch.getToken());
    assertEquals(match.getId(), mappedMatch.getId());
    assertEquals(match.getGame().getId(), mappedMatch.getGame().getId());
    assertEquals(matchEntity.getUsers(), mappedMatch.getUsers());
  }


}







