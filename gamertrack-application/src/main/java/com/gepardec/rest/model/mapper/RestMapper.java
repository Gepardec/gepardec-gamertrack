package com.gepardec.rest.model.mapper;

import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateGameOutcomeCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateGameOutcomeCommand;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestMapper {
    public ScoreDto toScore(CreateScoreCommand scoreCommand) {
        return new ScoreDto(0,scoreCommand.userId(), scoreCommand.gameId(), scoreCommand.scorePoints());
    }
    public UserDto CreateUserCommandtoUser(CreateUserCommand createUserCommand) {
        return new UserDto(0,createUserCommand.firstname(), createUserCommand.lastname(), false);
    }
    public UserDto UpdateUserCommandtoUser(Long id, UpdateUserCommand updateUserCommand) {
        return new UserDto(id,updateUserCommand.firstname(), updateUserCommand.lastname(), updateUserCommand.deactivated());
    }


  public GameOutcomeDto toGameOutcomeDto(Long id, UpdateGameOutcomeCommand gameOutcomeCommand) {
    return new GameOutcomeDto(id, gameOutcomeCommand.gameId(),
        gameOutcomeCommand.userIds());
  }

  public GameOutcomeDto toGameOutcomeDto(CreateGameOutcomeCommand createGameCommand) {
    return new GameOutcomeDto(null, createGameCommand.gameId(),
        createGameCommand.userIds());
  }

  public GameDto toGameDto(Long id, UpdateGameCommand updateGameCommand) {
    return new GameDto(id, updateGameCommand.title(),
        updateGameCommand.rules());
  }

  public GameDto toGameDto(CreateGameCommand createGameCommand) {
    return new GameDto(0L, createGameCommand.title(), createGameCommand.rules());
  }
}
