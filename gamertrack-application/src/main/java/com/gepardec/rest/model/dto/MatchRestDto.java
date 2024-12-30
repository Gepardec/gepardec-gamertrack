package com.gepardec.rest.model.dto;

import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import java.util.List;

public record MatchRestDto(Long id, Game game, List<User> users) {


  public MatchRestDto(Match match) {
    this(match.getId(),
        match.getGame(), match.getUsers());
  }
}
