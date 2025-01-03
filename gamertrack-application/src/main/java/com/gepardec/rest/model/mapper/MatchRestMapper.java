package com.gepardec.rest.model.mapper;

import com.gepardec.model.Match;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MatchRestMapper {

  public Match updateMatchCommandtoMatch(Long id, String token,
      UpdateMatchCommand updateMatchCommand) {
    return new Match(id, token, updateMatchCommand.game(),
        updateMatchCommand.users());
  }

  public Match createMatchCommandtoMatch(CreateMatchCommand createMatchCommand) {
    return new Match(null, null, createMatchCommand.game(), createMatchCommand.users());
  }
}
