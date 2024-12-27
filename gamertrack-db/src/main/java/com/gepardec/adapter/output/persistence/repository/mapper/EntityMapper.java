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
import java.util.stream.Collectors;

@ApplicationScoped
public class EntityMapper {

  @PersistenceContext()
  protected EntityManager entityManager;

  public UserEntity UserModelToUserEntity(User user) {
    return new UserEntity(user.getFirstname(), user.getLastname(), user.isDeactivated());
  }

  public User UserEntityToUserModel(UserEntity userEntity) {
    return new User(userEntity.getId(), userEntity.getFirstname(), userEntity.getLastname(),
        userEntity.isDeactivated());
  }

  public UserEntity UserModeltoExistingUserEntity(User user, UserEntity userEntity) {
    userEntity.setFirstname(user.getFirstname());
    userEntity.setLastname(user.getLastname());
    userEntity.setDeactivated(user.isDeactivated());
    return userEntity;
  }

  public ScoreEntity ScoreModeltoScoreEntity(Score score) {
    return new ScoreEntity(entityManager.getReference(UserEntity.class, score.getUser().getId()),
        entityManager.getReference(GameEntity.class, score.getGame().getId()),
        score.getScorePoints());
  }

  public Score ScoreEntityToScoreModel(ScoreEntity scoreEntity) {
    return new Score(scoreEntity.getId(), UserEntityToUserModel(scoreEntity.getUser()),
        gameEntityToGameModel(scoreEntity.getGame()), scoreEntity.getScorePoints());
  }

  public ScoreEntity ScoreModeltoExistingScoreEntity(Score score, ScoreEntity scoreEntity) {
    scoreEntity.setUser(entityManager.getReference(UserEntity.class, score.getUser().getId()));
    scoreEntity.setGame(entityManager.getReference(GameEntity.class, score.getGame().getId()));
    scoreEntity.setScorePoints(score.getScorePoints());
    return scoreEntity;
  }


  public MatchEntity matchModelToMatchEntityWithReference(Match match) {
    return matchModelToMatchEntityWithReference(match, new MatchEntity());
  }

  public Match matchEntityToMatchModel(MatchEntity matchEntity) {
    return new Match(matchEntity.getId(), gameEntityToGameModel(matchEntity.getGame()),
        matchEntity.getUsers().stream().map(this::UserEntityToUserModel).toList());
  }

  public MatchEntity matchModelToMatchEntity(Match match) {
    List<UserEntity> users = new ArrayList<>();
    match.getUsers().forEach(user -> users.add(
        new UserEntity(user.getId(), user.getFirstname(), user.getLastname(),
            user.isDeactivated())));

    return new MatchEntity(gameModelToGameEntity(match.getGame()), users);
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

  public GameEntity gameModelToGameEntity(Game game) {
    return game.getId() != null
        ? new GameEntity(game.getId(), game.getName(), game.getRules())
        : new GameEntity(null, game.getName(), game.getRules());
  }

  public Game gameEntityToGameModel(GameEntity gameEntity) {
    return gameEntity.getId() != null
        ? new Game(gameEntity.getId(), gameEntity.getName(), gameEntity.getRules())
        : new Game(null, gameEntity.getName(), gameEntity.getRules());
  }

  public GameEntity gameModelToExitstingGameEntity(Game game, GameEntity gameEntity) {
    gameEntity.setRules(game.getRules());
    gameEntity.setName(game.getName());
    return gameEntity;
  }

}
