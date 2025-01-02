package com.gepardec.rest.model.mapper;

import com.gepardec.model.Match;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;

public class MatchRestMapper {

  public Match updateMatchCommandtoMatch(Long id, UpdateMatchCommand gameOutcomeCommand) {
    return new Match(id, gameOutcomeCommand.game(),
        gameOutcomeCommand.users());
  }

  public Match createMatchCommandtoMatch(CreateMatchCommand createGameCommand) {
    return new Match(null, createGameCommand.game(), createGameCommand.users());
  }
}
