package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.List;

public class TestFixtures {

  public static Game game() {
    return new Game("Game Fixture", "Game Fixture Rules");
  }

  public static GameOutcome gameOutcome() {
    return new GameOutcome(TestFixtures.game(), List.of(TestFixtures.user()));
  }

  public static User user() {
    return new User("User", "Testfixture");
  }

  public static GameOutcome gameOutcome(Game game, User... user) {
    return new GameOutcome(game, List.of(user));
  }



}
