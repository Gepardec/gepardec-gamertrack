package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.GameDto;
import com.gepardec.model.dto.MatchDto;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {


  public static List<Game> games(int gameCount) {
    List<Game> games = new ArrayList<>();

    for (int i = 0; i < gameCount; i++) {
      Game game = TestFixtures.game((long) 1 + i);
      game.setName(game.getName() + " " + i);
      games.add(game);
    }
    return games;
  }

  public static Game game() {
    return game(1L);
  }

  public static Game game(Long id) {
    Game game = new Game("Game Fixture", "Game Fixture Rules");
    game.setId(id);
    return game;
  }

  public static GameDto gameToGameDto(Game game) {
    return new GameDto(game.getId(), game.getName(), game.getRules());
  }

  public static MatchDto matchToMatchDto(Match match) {
    return new MatchDto(match.getId(), match.getGame().getId(),
        match.getUsers().stream().map(User::getId).toList());
  }

  public static Game gameDtoToGame(GameDto gameDto) {
    Game game = new Game(gameDto.title(), gameDto.rules());

    game.setId(gameDto.id());

    return game;
  }


  public static Match match() {
    return match(1L, TestFixtures.game(), TestFixtures.users(10));
  }


  public static Match match(Long id, Game game, List<User> users) {
    Match match = new Match(game, users.stream().toList());
    match.setId(id);

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
    User user = new User("User", "Testfixture");
    user.setId(id);
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
    Score score = new Score(user(userId), game(gameId), 10L);
    score.setId(scoreId);
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