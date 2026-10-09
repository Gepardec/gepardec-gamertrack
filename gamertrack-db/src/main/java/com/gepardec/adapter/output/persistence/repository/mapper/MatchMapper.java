package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Match;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class MatchMapper {

  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private UserMapper userMapper;
  @Inject
  private MatchMapper matchMapper;
  @Inject
  private GameMapper gameMapper;

  public MatchEntity matchModelToMatchEntityWithReference(Match match) {
    return matchModelToMatchEntityWithReference(match, new MatchEntity());
  }

  public Match matchEntityToMatchModel(MatchEntity matchEntity) {

    Match match = new Match(matchEntity.getId(), matchEntity.getToken(),
            matchEntity.getCreatedOn(), matchEntity.getUpdatedOn(),
            gameMapper.gameEntityToGameModel(matchEntity.getGame()),
        matchEntity.getUsers().stream().map(userMapper::userEntityToUserModel).toList());
    match.setOutcome(matchEntity.getPlacements().entrySet().stream()
        .collect(Collectors.toMap(placement -> placement.getKey().getToken(),
            Map.Entry::getValue)));
    return match;
  }

  public MatchEntity matchModelToMatchEntity(Match match) {
    List<UserEntity> users = new ArrayList<>();
    match.getUsers().forEach(user -> users.add(
        new UserEntity(user.getId(), user.getFirstname(), user.getLastname(),
            user.isDeactivated(), user.getToken())));

    MatchEntity matchEntity = new MatchEntity(match.getId(), match.getToken(),
        gameMapper.gameModelToGameEntity(match.getGame()), users);
    matchEntity.setPlacements(users.stream()
        .filter(user -> match.getOutcome().containsKey(user.getToken()))
        .collect(Collectors.toMap(user -> user,
            user -> match.getOutcome().get(user.getToken()))));
    return matchEntity;
  }

  public MatchEntity matchModelToMatchEntityWithReference(Match match, MatchEntity matchEntity) {

    matchEntity.setGame(
        entityManager.getReference(GameEntity.class, match.getGame().getId()));
    matchEntity.setUsers(
        match.getUsers().stream()
            .map(u -> entityManager.getReference(UserEntity.class, u.getId()))
            .collect(Collectors.toList()));
    matchEntity.getPlacements().clear();
    match.getUsers().stream()
        .filter(u -> match.getOutcome().containsKey(u.getToken()))
        .forEach(u -> matchEntity.getPlacements().put(
            entityManager.getReference(UserEntity.class, u.getId()),
            match.getOutcome().get(u.getToken())));
    matchEntity.setToken(match.getToken());
    return matchEntity;
  }


}
