package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import java.util.ArrayList;
import java.util.List;

public class TestFixtures {

  public static List<Game> games(int gameCount) {
    List<Game> games = new ArrayList<>();

    for (int i = 0; i < gameCount; i++) {
      games.add(TestFixtures.game((long) i++));
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

  public static GameOutcomeDto gameOutcometoGameOutcomeDto(GameOutcome gameOutcome) {
    return new GameOutcomeDto(gameOutcome.getId(), gameOutcome.getGame().getId(),
        gameOutcome.getUsers().stream().map(User::getId).toList());
  }

  public static Game gameDtoToGame(GameDto gameDto) {
    Game game = new Game(gameDto.title(), gameDto.rules());

    game.setId(gameDto.id());

    return game;
  }


  public static GameOutcome gameOutcome() {
    return gameOutcome(1L, TestFixtures.game(), TestFixtures.users(10));
  }


  public static GameOutcome gameOutcome(Long id, Game game, List<User> users) {
    GameOutcome gameOutcome = new GameOutcome(game, users.stream().toList());
    gameOutcome.setId(id);

    return gameOutcome;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>();

    for (int i = 0; i < userCount; i++) {
      users.add(TestFixtures.user((long) i++));
    }

    return users;
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


  public static List<GameOutcome> gameOutcomes(int gameOutcomeCount) {
    List<GameOutcome> gameOutcomes = new ArrayList<>();
    for (int i = 0; i < gameOutcomeCount; i++) {
      gameOutcomes.add(gameOutcome((long) i++));
    }
    return gameOutcomes;
  }

  public static Score score(Long scoreId, Long userId, Long gameId) {
    Score score = new Score(user(userId),game(gameId),10L);
    score.setId(scoreId);
    return score;
  }


  public static List<Score> scores(int scoreCount) {
    List<Score> scores = new ArrayList<>();
    for (int i = 0; i < scoreCount; i++) {
      scores.add(score((long) i,(long) i,1L));
    }
    return scores;
  }


}