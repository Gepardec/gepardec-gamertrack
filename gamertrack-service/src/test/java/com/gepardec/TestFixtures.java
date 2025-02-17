package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {

  public static Game game() {
    return game(1L);
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
    return gameOutcome(1L, TestFixtures.game(), TestFixtures.users(10));
  }

  public static User user() {
    return user(1L);
  }

  public static User user(Long id) {
    User user = new User("User", "Testfixture");
    user.setId(id);
    return user;
  }

  public static GameOutcome gameOutcome(Long gameOutComeId) {
    GameOutcome gameOutcome = gameOutcome();
    gameOutcome.setId(gameOutComeId);

    return gameOutcome;
  }

  public static GameOutcome gameOutcome(Long id, Game game, List<User> users) {
    GameOutcome gameOutcome = new GameOutcome(game, users);
    gameOutcome.setId(id);
    return gameOutcome;
  }

  public static List<GameOutcome> gameOutcomes(int gameOutcomeCount) {
    List<GameOutcome> gameOutcomes = new ArrayList<>();
    for (int i = 0; i < gameOutcomeCount; i++) {
      gameOutcomes.add(gameOutcome((long) i++));
    }
    return gameOutcomes;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>();

    for (int i = 0; i < userCount; i++) {
      users.add(TestFixtures.user((long) i++));
    }

    return users;
  }

  public static List<Long> userIds(int userCount) {
    return users(userCount).stream().map(User::getId).toList();
  }

}
