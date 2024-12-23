package com.gepardec.adapter.output.persistence.repository.mapper;

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
public class Mapper {

  @PersistenceContext()
  protected EntityManager entityManager;

  public com.gepardec.adapter.output.persistence.entity.User toUser(User user) {
    return new com.gepardec.adapter.output.persistence.entity.User(user.firstname(), user.lastname(), user.deactivated());
  }

  public com.gepardec.adapter.output.persistence.entity.User toExistingUser(User userDTO, com.gepardec.adapter.output.persistence.entity.User user) {
    user.setFirstname(userDTO.firstname());
    user.setLastname(userDTO.lastname());
    user.setDeactivated(userDTO.deactivated());
    return user;
  }

  public com.gepardec.adapter.output.persistence.entity.Score toScore(Score score) {
    return new com.gepardec.adapter.output.persistence.entity.Score(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, score.userId()),
        entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, score.gameId()), score.scorePoints());
  }

  public com.gepardec.adapter.output.persistence.entity.Score toExistingScore(Score scoreDto, com.gepardec.adapter.output.persistence.entity.Score score) {
    score.setUser(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, scoreDto.userId()));
    score.setGame(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, scoreDto.gameId()));
    score.setScorePoints(scoreDto.scorePoints());
    return score;
  }


  public com.gepardec.adapter.output.persistence.entity.Match toMatchWithReference(Match match) {
    return toMatchWithReference(match, new com.gepardec.adapter.output.persistence.entity.Match());
  }

  public com.gepardec.adapter.output.persistence.entity.Match toMatchWithReference(Match matchDto,
                                                                                   com.gepardec.adapter.output.persistence.entity.Match match) {
    if (matchDto == null && match == null) {
      return null;
    }

    List<com.gepardec.adapter.output.persistence.entity.User> users = new ArrayList<>();

    if (matchDto.id() != null) {
      match.setId(matchDto.id());
    }

    matchDto.userIds()
        .forEach(userid -> users.add(
            entityManager.getReference(com.gepardec.adapter.output.persistence.entity.User.class, userid)));

    match.setGame(entityManager.getReference(com.gepardec.adapter.output.persistence.entity.Game.class, matchDto.gameId()));
    match.setUsers(users);
    return match;
  }

  public com.gepardec.adapter.output.persistence.entity.Game toGame(Game game) {
    return toGame(game, new com.gepardec.adapter.output.persistence.entity.Game());
  }

  public com.gepardec.adapter.output.persistence.entity.Game toGame(Game gameDto, com.gepardec.adapter.output.persistence.entity.Game game) {
    game.setRules(gameDto.rules());
    game.setName(gameDto.title());

    return game;
  }

}
