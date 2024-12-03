package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {

  public static Game game() {
    Game game = new Game("Game Fixture", "Game Fixture Rules");
    game.setId(1L);

    return game;
  }

  public static Game game(Long id) {
    Game game = new Game("Game Fixture", "Game Fixture Rules");
    game.setId(id);
    return game;
  }

  public static List<Game> games(int gameCount) {
    List<Game> games = new ArrayList<>();

    for (int i = 0; i < gameCount; i++) {
      games.add(TestFixtures.game((long) i++));
    }
    return games;
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
