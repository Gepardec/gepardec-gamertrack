package com.gepardec.rest.model.mapper;

import com.gepardec.model.Match;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;

public class MatchRestMapper {

  public Match updateMatchCommandtoMatch(Long id, String token,
      UpdateMatchCommand gameOutcomeCommand) {
    return new Match(id, token, gameOutcomeCommand.game(),
        gameOutcomeCommand.users());
  }

  public Match createMatchCommandtoMatch(CreateMatchCommand createGameCommand) {
    return new Match(null, null, createGameCommand.game(), createGameCommand.users());
  }
}
