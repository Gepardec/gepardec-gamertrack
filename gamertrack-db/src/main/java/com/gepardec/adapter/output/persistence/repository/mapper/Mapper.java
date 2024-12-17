package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.model.dto.GameDto;
import com.gepardec.model.dto.MatchDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Mapper {

  @PersistenceContext()
  protected EntityManager entityManager;

  public User toUser(UserDto userDTO) {
    return new User(userDTO.firstname(), userDTO.lastname(), userDTO.deactivated());
  }

  public User toExistingUser(UserDto userDTO, User user) {
    user.setFirstname(userDTO.firstname());
    user.setLastname(userDTO.lastname());
    user.setDeactivated(userDTO.deactivated());
    return user;
  }

  public Score toScore(ScoreDto scoreDto) {
    return new Score(entityManager.getReference(User.class, scoreDto.userId()),
        entityManager.getReference(Game.class, scoreDto.gameId()), scoreDto.scorePoints());
  }

  public Score toExistingScore(ScoreDto scoreDto, Score score) {
    score.setUser(entityManager.getReference(User.class, scoreDto.userId()));
    score.setGame(entityManager.getReference(Game.class, scoreDto.gameId()));
    score.setScorePoints(scoreDto.scorePoints());
    return score;
  }


  public Match toMatchWithReference(MatchDto gameOutcomeDto) {
    return toMatchWithReference(gameOutcomeDto, new Match());
  }

  public Match toMatchWithReference(MatchDto gameOutcomeDto,
      Match gameOutcome) {
    if (gameOutcomeDto == null && gameOutcome == null) {
      return null;
    }

    List<User> users = new ArrayList<>();

    if (gameOutcomeDto.id() != null) {
      gameOutcome.setId(gameOutcomeDto.id());
    }

    gameOutcomeDto.userIds()
        .forEach(userid -> users.add(
            entityManager.getReference(User.class, userid)));

    gameOutcome.setGame(entityManager.getReference(Game.class, gameOutcomeDto.gameId()));
    gameOutcome.setUsers(users);
    return gameOutcome;
  }

  public Game toGame(GameDto gameDto) {
    return toGame(gameDto, new Game());
  }

  public Game toGame(GameDto gameDto, Game game) {
    game.setRules(gameDto.rules());
    game.setName(gameDto.title());

    return game;
  }

}
