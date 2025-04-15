package com.gepardec.rest.model.dto;

import com.gepardec.model.Game;

public record GameRestDto(String token, String name, String rules) {

  public GameRestDto(Game Game) {
    this(Game.getToken(), Game.getName(), Game.getRules());
  }
}
