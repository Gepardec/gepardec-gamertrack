package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {


  public static List<Game> games(int gameCount) {
    List<Game> games = new ArrayList<>();

    for (int i = 0; i < gameCount; i++) {
      games.add(new Game(null, "TestGameTitle" + i, "TestGameRules" + i));
    }
    return games;
  }

  public static Game game() {
    return game(1L);
  }

  public static Game game(Long id) {
    return new Game(id, "Game Fixture", "Game Fixture Rules");
  }

  public static Match match() {
    return match(1L, TestFixtures.game(), TestFixtures.users(10));
  }


  public static Match match(Long id, Game game, List<User> users) {
    Match match = new Match(id, game, users.stream().toList());
    return match;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>(userCount);

    for (int i = 0; i <= userCount; i++) {
      users.add(new User(null, "FirstName" + i, "LastName" + 1, false,null));

    }

    return users;
  }

  public static List<User> usersWithId(int userCount) {
    List<User> users = new ArrayList<>(userCount);

    for (int i = 0; i <= userCount; i++) {
      users.add(new User((long) i + 1, "FirstName" + i, "LastName" + i, true,null));
    }
    return users;
  }

  public static User user(Long id) {
    User user = new User(id, "User", "Testfixture", false,null);
    return user;
  }

  public static Match match(Long matchId) {
    Match match = match();
    match.setId(matchId);

    return match;
  }


  public static List<Match> matches(int matchCount) {
    List<Match> matches = new ArrayList<>();
    for (int i = 0; i < matchCount; i++) {
      Match match = match((long) i + 1);
      match.getGame().setName(String.valueOf(i + 1));
      matches.add(match);
    }
    return matches;
  }

  public static Score score(Long scoreId, Long userId, Long gameId) {
    Score score = new Score(scoreId, user(userId), game(gameId), 10L, null);
    return score;
  }


  public static List<Score> scores(int scoreCount) {
    List<Score> scores = new ArrayList<>();
    for (int i = 0; i < scoreCount; i++) {
      scores.add(score((long) i, (long) i, 1L));
    }
    return scores;
  }


}