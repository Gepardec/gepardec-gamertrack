package com.gepardec.rest.model.mapper;

import com.gepardec.model.Game;
import com.gepardec.model.Tournament;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateTournamentCommand;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TournamentRestMapper {

  public Tournament createTournamentCommandToTournament(CreateTournamentCommand command) {
    return new Tournament(null, null, command.name(),
        new Game(null, command.gameToken(), null, null),
        usersFromTokens(command.participantTokens()), null);
  }

  public List<User> usersFromTokens(List<String> userTokens) {
    return userTokens == null ? null
        : userTokens.stream()
            .map(token -> new User(null, null, null, false, token))
            .toList();
  }
}
