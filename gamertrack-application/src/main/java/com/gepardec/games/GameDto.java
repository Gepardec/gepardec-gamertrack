package com.gepardec.games;

import com.gepardec.model.Game;

public record GameDto(Long id , String name, String rules) {

  public GameDto(Game Game) {
    this(Game.getId(), Game.getName(), Game.getRules());
  }
}
