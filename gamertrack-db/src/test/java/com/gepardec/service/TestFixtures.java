package com.gepardec.service;

import com.gepardec.model.Game;

public class TestFixtures {

  public static Game getGame() {
    return new Game("Example Game", "Fake Rules");
  }

}
