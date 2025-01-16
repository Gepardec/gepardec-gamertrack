package com.gepardec.rest.model.dto;

import com.gepardec.model.Game;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameRestDto(@NotNull Long id, @NotBlank String name, String rules) {

  public GameRestDto(Game game) {
    this(game.getId(), game.getName(), game.getRules());
  }
}
