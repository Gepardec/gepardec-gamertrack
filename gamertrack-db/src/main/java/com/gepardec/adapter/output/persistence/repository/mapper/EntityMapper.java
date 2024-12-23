package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.ScoreEntity;
import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EntityMapper {

  @PersistenceContext()
  protected EntityManager entityManager;

  public UserEntity UserModelToUserEntity(User user) {
    return new UserEntity(user.getFirstname(), user.getLastname(), user.isDeactivated());
  }

  public User UserEntityToUserModel(UserEntity userEntity) {
    return new User(userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(), userEntity.isDeactivated());
  }

  public UserEntity UserModeltoExistingUserEntity(User user, UserEntity userEntity) {
    userEntity.setFirstname(user.getFirstname());
    userEntity.setLastname(user.getLastname());
    userEntity.setDeactivated(user.isDeactivated());
    return userEntity;
  }

  public ScoreEntity ScoreModeltoScoreEntity(Score score) {
    return new ScoreEntity(entityManager.getReference(UserEntity.class, score.getUser().getId()),
        entityManager.getReference(GameEntity.class, score.getGame().getId()), score.getScorePoints());
  }
  public Score ScoreEntityToScoreModel(ScoreEntity scoreEntity) {
    return new Score(scoreEntity.getId(), UserEntityToUserModel(scoreEntity.getUser()), GameEntityToGameModel(scoreEntity.getGame()), scoreEntity.getScorePoints());
  }

  public ScoreEntity ScoreModeltoExistingScoreEntity(Score score, ScoreEntity scoreEntity) {
    scoreEntity.setUser(entityManager.getReference(UserEntity.class, score.getUser().getId()));
    scoreEntity.setGame(entityManager.getReference(GameEntity.class, score.getGame().getId()));
    scoreEntity.setScorePoints(score.getScorePoints());
    return scoreEntity;
  }


  public MatchEntity MatchModeltoModelEntiyWithReference(Match match) {
    return MatchModeltoMatchEntityWithReference(match, new MatchEntity());
  }

  public Match MatchEntityToMatchModel(MatchEntity matchEntity) {
    return new Match(matchEntity.getId(),GameEntityToGameModel(matchEntity.getGame()),matchEntity.getUsers().stream().map(this::UserEntityToUserModel).toList());
  }

  public MatchEntity MatchModeltoMatchEntityWithReference(Match match, MatchEntity matchEntity) {
    if (match == null && match == null) {
      return null;
    }

    List<UserEntity> users = new ArrayList<>();

    if (match.getId() != null) {
      match.setId(match.getId());
    }

    matchEntity.setGame(GameModelToGameEntity(match.getGame()));
    matchEntity.setUsers(match.getUsers().stream().map(this::UserModelToUserEntity).toList());
    return matchEntity;
  }

  public GameEntity GameModelToGameEntity(Game game) {
    return new GameEntity(game.getRules(), game.getTitle());
  }

  public Game GameEntityToGameModel(GameEntity gameEntity) {
    return new Game(gameEntity.getId(),gameEntity.getName(),gameEntity.getRules());
  }

  public GameEntity GameModelToExitstingGameEntity(Game game, GameEntity gameEntity) {
    gameEntity.setRules(game.getRules());
    gameEntity.setName(game.getTitle());

    return gameEntity;
  }

}
