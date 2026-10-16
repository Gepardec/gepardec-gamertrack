package com.gepardec.adapter.output.persistence.repository.mapper;

import static com.gepardec.TestFixtures.tournament;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.TournamentEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Tournament;
import com.gepardec.model.TournamentState;
import com.gepardec.model.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TournamentMapperTest {

  @Mock
  EntityManager entityManager;

  @Mock
  GameMapper gameMapper = new GameMapper();

  @Mock
  UserMapper userMapper = new UserMapper();

  @InjectMocks
  TournamentMapper tournamentMapper = new TournamentMapper();


  @Test
  void ensureTournamentModelToTournamentEntityWithReferenceMappingWorks() {
    Tournament tournament = tournament();

    when(entityManager.getReference(eq(GameEntity.class), anyLong()))
        .thenReturn(new GameEntity(tournament.getGame().getId(), tournament.getGame().getToken(),
            tournament.getGame().getName(), tournament.getGame().getRules()));
    when(entityManager.getReference(eq(UserEntity.class), any())).thenAnswer(
        invocation -> {
          Long userId = invocation.getArgument(1);
          User user = tournament.getParticipants().stream()
              .filter(participant -> participant.getId().equals(userId))
              .findFirst().orElseThrow();
          return new UserEntity(user.getId(), user.getFirstname(), user.getLastname(),
              user.isDeactivated(), user.getToken());
        });

    TournamentEntity mappedTournament =
        tournamentMapper.tournamentModelToTournamentEntityWithReference(tournament);

    assertEquals(tournament.getToken(), mappedTournament.getToken());
    assertEquals(tournament.getName(), mappedTournament.getName());
    assertEquals(tournament.getState(), mappedTournament.getState());
    assertEquals(tournament.getGame().getId(), mappedTournament.getGame().getId());
    assertTrue(tournament.getParticipants().stream().map(User::getId).toList()
        .containsAll(
            mappedTournament.getParticipants().stream().map(UserEntity::getId).toList()));
  }

  @Test
  void ensureTournamentEntityToTournamentModelMappingWorks() {
    Tournament tournament = tournament();

    GameEntity gameEntity = new GameEntity(tournament.getGame().getId(),
        tournament.getGame().getToken(), tournament.getGame().getName(),
        tournament.getGame().getRules());
    List<UserEntity> userEntities = tournament.getParticipants().stream()
        .map(user -> new UserEntity(user.getId(), user.getFirstname(), user.getLastname(),
            user.isDeactivated(), user.getToken()))
        .toList();
    TournamentEntity tournamentEntity = new TournamentEntity(tournament.getId(),
        tournament.getToken(), tournament.getName(), TournamentState.RUNNING, gameEntity,
        userEntities);

    when(gameMapper.gameEntityToGameModel(gameEntity)).thenReturn(tournament.getGame());
    when(userMapper.userEntityToUserModel(any(UserEntity.class))).thenAnswer(
        invocation -> {
          UserEntity userEntity = invocation.getArgument(0);
          return new User(userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(),
              userEntity.isDeactivated(), userEntity.getToken());
        });

    Tournament mappedTournament =
        tournamentMapper.tournamentEntityToTournamentModel(tournamentEntity);

    assertEquals(tournament.getId(), mappedTournament.getId());
    assertEquals(tournament.getToken(), mappedTournament.getToken());
    assertEquals(tournament.getName(), mappedTournament.getName());
    assertEquals(TournamentState.RUNNING, mappedTournament.getState());
    assertEquals(tournament.getGame(), mappedTournament.getGame());
    assertEquals(tournament.getParticipants().stream().map(User::getToken).toList(),
        mappedTournament.getParticipants().stream().map(User::getToken).toList());
  }
}
