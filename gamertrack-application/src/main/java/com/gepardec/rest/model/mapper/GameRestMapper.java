package com.gepardec.rest.model.mapper;

import com.gepardec.model.Game;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameRestMapper {


  public Game updateGameCommandtoGame(String token, UpdateGameCommand updateGameCommand) {
    return new Game(null, token, updateGameCommand.name(), updateGameCommand.rules());
  }

  public Game createGameCommandtoGame(CreateGameCommand createGameCommand) {
    return new Game(null, null, createGameCommand.name(), createGameCommand.rules());
  }
}
