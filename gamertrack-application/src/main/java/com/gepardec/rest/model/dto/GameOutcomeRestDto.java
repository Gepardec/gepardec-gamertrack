package com.gepardec.rest.model.dto;

import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.List;

public record GameOutcomeRestDto(Long id, Long gameId, List<Long> userIds) {


  public GameOutcomeRestDto(GameOutcome gameOutcome) {
    this(gameOutcome.getId(),
        gameOutcome.getGame().getId(), gameOutcome.getUsers().stream().map(User::getId).toList());
  }
}
