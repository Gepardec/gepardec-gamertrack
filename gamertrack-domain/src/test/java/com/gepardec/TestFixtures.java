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
      Game game = TestFixtures.game((long) 1 + i);
      game.setTitle(game.getTitle() + " " + i);
      games.add(game);
    }
    return games;
  }

  public static Game game() {
    return game(1L);
  }

  public static Game game(Long id) {
    Game game = new Game(id,"Game Fixture", "Game Fixture Rules");
    return game;
  }

  public static Game gameToGameDto(Game game) {
    return new Game(game.getId(), game.getTitle(), game.getRules());
  }

  public static Match matchToMatchDto(Match match) {
    return new Match(match.getId(), match.getGame(),
        match.getUsers());
  }

  public static Game gameDtoToGame(Game gameDto) {
    Game game = new Game(gameDto.getId(),gameDto.getTitle(), gameDto.getRules());

    return game;
  }


  public static Match match() {
    return match(1L, TestFixtures.game(), TestFixtures.users(10));
  }


  public static Match match(Long id, Game game, List<User> users) {
    Match match = new Match(id,game, users.stream().toList());
    return match;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>(userCount);

    for (int i = 0; i <= userCount; i++) {
      User user = TestFixtures.user((long) i + 1);
      users.add(user);
    }

    return users;
  }

  public static User user(Long id) {
    User user = new User(id,"User", "Testfixture",false);
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
      match.getGame().setTitle(String.valueOf(i + 1));
      matches.add(match);
    }
    return matches;
  }

  public static Score score(Long scoreId, Long userId, Long gameId) {
    Score score = new Score(scoreId, user(userId), game(gameId), 10L);
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