package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateGameOutcomeCommand;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateGameOutcomeCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import com.gepardec.rest.model.dto.GameOutcomeRestDto;
import java.util.ArrayList;
import java.util.List;

public class RestTestFixtures {

  public static CreateScoreCommand createScoreCommand(Long id) {
    CreateScoreCommand createScoreCommand = new CreateScoreCommand(1L, 1L, 10);
    return createScoreCommand;
  }

  public static CreateUserCommand createUserCommand(Long id) {
    CreateUserCommand createUserCommand = new CreateUserCommand("Max", "Muster");
    return createUserCommand;
  }

  public static UpdateUserCommand updateUserCommand(Long id) {
    UpdateUserCommand updateUserCommand = new UpdateUserCommand("Max", "Muster", false);
    return updateUserCommand;
  }

  public static UpdateGameOutcomeCommand updateGameOutcomeCommand() {
    return new UpdateGameOutcomeCommand(game().getId(),
        users(5).stream().map(User::getId).toList());
  }

  public static CreateGameOutcomeCommand createGameOutcomeCommand() {
    return new CreateGameOutcomeCommand(game().getId(),
        users(5).stream().map(User::getId).toList());
  }

  public static CreateGameCommand createGameCommand() {
    return new CreateGameCommand(game().getName(), game().getRules());
  }


  public static GameOutcomeRestDto gameOutcomeRestDto(Long id, Long gameid, List<Long> userIds) {
    return new GameOutcomeRestDto(id, gameid, userIds);
  }

  public static Game game() {
    return game(1L);
  }

  public static Game game(Long id) {
    Game game = new Game("Game Fixture", "Game Fixture Rules");
    game.setId(id);
    return game;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>();

    for (int i = 0; i < userCount; i++) {
      users.add(TestFixtures.user((long) i++));
    }

    return users;
  }


  public static UpdateGameCommand updateGameCommand() {
    return new UpdateGameCommand(game().getName(), game().getRules());
  }
}
