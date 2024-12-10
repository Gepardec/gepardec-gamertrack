package com.gepardec.rest.model.dto;

import com.gepardec.model.Game;

public record GameRestDto(Long id, String name, String rules) {

  public GameRestDto(Game Game) {
    this(Game.getId(), Game.getName(), Game.getRules());
  }
}
