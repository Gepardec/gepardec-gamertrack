package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import com.gepardec.model.User;
import java.util.List;

public record MatchRestDto(Long id, Long gameId, List<Long> userIds) {


  public MatchRestDto(Match match) {
    this(match.getId(),
        match.getGame().getId(), match.getUsers().stream().map(User::getId).toList());
  }
}
