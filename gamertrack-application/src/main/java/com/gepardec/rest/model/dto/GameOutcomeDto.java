package com.gepardec.rest.model.dto;

import com.gepardec.model.AbstractEntity;
import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record GameOutcomeDto(Long id, Long gameId, List<Long> userIds) {


  public GameOutcomeDto(GameOutcome gameOutcome) {
    this(gameOutcome.getId(),
        gameOutcome.getGame().getId(), gameOutcome.getUsers().stream().map(User::getId).toList());
  }
}
