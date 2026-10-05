package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.TournamentEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Tournament;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.stream.Collectors;

@ApplicationScoped
public class TournamentMapper {

  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private UserMapper userMapper;
  @Inject
  private GameMapper gameMapper;

  public TournamentEntity tournamentModelToTournamentEntityWithReference(Tournament tournament) {
    return tournamentModelToTournamentEntityWithReference(tournament, new TournamentEntity());
  }

  public TournamentEntity tournamentModelToTournamentEntityWithReference(Tournament tournament,
      TournamentEntity tournamentEntity) {

    tournamentEntity.setToken(tournament.getToken());
    tournamentEntity.setName(tournament.getName());
    tournamentEntity.setState(tournament.getState());
    tournamentEntity.setGame(
        entityManager.getReference(GameEntity.class, tournament.getGame().getId()));
    tournamentEntity.setParticipants(
        tournament.getParticipants().stream()
            .map(u -> entityManager.getReference(UserEntity.class, u.getId()))
            .collect(Collectors.toList()));
    return tournamentEntity;
  }

  public Tournament tournamentEntityToTournamentModel(TournamentEntity tournamentEntity) {

    return new Tournament(tournamentEntity.getId(), tournamentEntity.getToken(),
        tournamentEntity.getName(),
        gameMapper.gameEntityToGameModel(tournamentEntity.getGame()),
        tournamentEntity.getParticipants().stream().map(userMapper::userEntityToUserModel).toList(),
        tournamentEntity.getState(),
        tournamentEntity.getCreatedOn(), tournamentEntity.getUpdatedOn());
  }
}
