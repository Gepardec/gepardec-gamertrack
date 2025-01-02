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
    return new Match(matchEntity.getId(), gameMapper.gameEntityToGameModel(matchEntity.getGame()),
        matchEntity.getUsers().stream().map(userMapper::userEntityToUserModel).toList());
  }

  public MatchEntity matchModelToMatchEntity(Match match) {
    List<UserEntity> users = new ArrayList<>();
    match.getUsers().forEach(user -> users.add(
        new UserEntity(user.getId(), user.getFirstname(), user.getLastname(),
            user.isDeactivated(), user.getToken())));

    return new MatchEntity(gameMapper.gameModelToGameEntity(match.getGame()), users);
  }

  public MatchEntity matchModelToMatchEntityWithReference(Match match, MatchEntity matchEntity) {

    matchEntity.setGame(
        entityManager.getReference(GameEntity.class, match.getGame().getId()));
    matchEntity.setUsers(
        match.getUsers().stream()
            .map(u -> entityManager.getReference(UserEntity.class, u.getId()))
            .collect(Collectors.toList()));
    return matchEntity;
  }


}
