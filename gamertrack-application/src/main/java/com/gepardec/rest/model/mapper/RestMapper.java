package com.gepardec.rest.model.mapper;

import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
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

  public Score createScoreCommandtoScore(CreateScoreCommand scoreCommand) {
    return new Score(null,scoreCommand.user(), scoreCommand.game(), scoreCommand.scorePoints());
  }

  public User createUserCommandtoUser(CreateUserCommand createUserCommand) {
    return new User(null, createUserCommand.firstname(), createUserCommand.lastname(), false);
  }

  public User updateUserCommandtoUserDto(Long id, UpdateUserCommand updateUserCommand) {
    return new User(id, updateUserCommand.firstname(), updateUserCommand.lastname(),
        updateUserCommand.deactivated());
  }


  public Match updateMatchCommandtoMatch(Long id, UpdateMatchCommand gameOutcomeCommand) {
    return new Match(id, gameOutcomeCommand.game(),
        gameOutcomeCommand.users());
  }

  public Match createMatchCommandtoMatch(CreateMatchCommand createGameCommand) {
    return new Match(null, createGameCommand.game(), createGameCommand.users());
  }

  public Game updateGameCommandtoGameDto(Long id, UpdateGameCommand updateGameCommand) {
    return new Game(id, updateGameCommand.title(), updateGameCommand.rules());
  }

  public Game createGameCommandtoGameDto(CreateGameCommand createGameCommand) {
    return new Game(null, createGameCommand.title(), createGameCommand.rules());
  }
}
