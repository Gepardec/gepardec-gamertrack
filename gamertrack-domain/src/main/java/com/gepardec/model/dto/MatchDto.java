package com.gepardec.model.dto;

import com.gepardec.model.Match;
import com.gepardec.model.User;
import java.util.List;
import java.util.stream.Collectors;

public record MatchDto(Long id, Long gameId, List<Long> userIds) {

  public MatchDto(Match gameOutcome) {
    this(gameOutcome.getId(), gameOutcome.getGame().getId(),
        gameOutcome.getUsers().stream().map(User::getId).collect(
            Collectors.toList()));
  }

}
