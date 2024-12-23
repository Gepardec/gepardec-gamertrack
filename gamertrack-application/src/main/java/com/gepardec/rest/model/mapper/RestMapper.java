package com.gepardec.rest.model.mapper;

import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestMapper {

  public Score toScoreDto(CreateScoreCommand scoreCommand) {

    return new ScoreDto(scoreCommand.userId(), scoreCommand.gameId());
  }

  public User CreateUserCommandtoUser(CreateUserCommand createUserCommand) {
    return new User(0, createUserCommand.firstname(), createUserCommand.lastname(), false);
  }

  public User UpdateUserCommandtoUser(Long id, UpdateUserCommand updateUserCommand) {
    return new User(id, updateUserCommand.firstname(), updateUserCommand.lastname(),
        updateUserCommand.deactivated());
  }


  public Match toMatchDto(Long id, UpdateMatchCommand gameOutcomeCommand) {
    return new Match(id, gameOutcomeCommand.gameId(),
        gameOutcomeCommand.userIds());
  }

  public Match toMatchDto(CreateMatchCommand createGameCommand) {
    return new Match(null, createGameCommand.gameId(),
        createGameCommand.userIds());
  }

  public Game toGameDto(Long id, UpdateGameCommand updateGameCommand) {
    return new Game(id, updateGameCommand.title(),
        updateGameCommand.rules());
  }

  public Game toGameDto(CreateGameCommand createGameCommand) {
    return new Game(0L, createGameCommand.title(), createGameCommand.rules());
  }
}
