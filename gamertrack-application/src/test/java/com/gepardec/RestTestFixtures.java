package com.gepardec;

import static com.gepardec.TestFixtures.user;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateGameCommand;
import com.gepardec.rest.model.command.CreateMatchCommand;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateGameCommand;
import com.gepardec.rest.model.command.UpdateMatchCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import com.gepardec.rest.model.dto.GameRestDto;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.UserRestDto;
import java.util.ArrayList;
import java.util.List;

public class RestTestFixtures {

  public static CreateScoreCommand createScoreCommand() {
    CreateScoreCommand createScoreCommand = new CreateScoreCommand(user(1L), game(), 10);
    return createScoreCommand;
  }

  public static CreateUserCommand createUserCommand() {
    CreateUserCommand createUserCommand = new CreateUserCommand("Max", "Muster");
    return createUserCommand;
  }

  public static UpdateUserCommand updateUserCommand() {
    UpdateUserCommand updateUserCommand = new UpdateUserCommand("Max", "Muster", false);
    return updateUserCommand;
  }

  public static UpdateMatchCommand updateMatchCommand() {
    return new UpdateMatchCommand(game(), users(5));
  }

  public static CreateMatchCommand createMatchCommand() {
    return new CreateMatchCommand(game(), users(5));
  }

  public static CreateGameCommand createGameCommand() {
    return new CreateGameCommand(game().getName(), game().getRules());
  }


  public static MatchRestDto matchRestDto(Long id, GameRestDto gameRestDto,
      List<UserRestDto> userRestDtos) {
    return new MatchRestDto(id, gameRestDto, userRestDtos);
  }

  public static Game game() {
    return game(1L);
  }

  public static Game game(Long id) {
    Game game = new Game(null, "Game Fixture", "Game Fixture Rules");
    game.setId(id);
    return game;
  }

  public static List<User> users(int userCount) {
    List<User> users = new ArrayList<>();

    for (int i = 0; i < userCount; i++) {
      users.add(user((long) i++));
    }

    return users;
  }


  public static UpdateGameCommand updateGameCommand() {
    return new UpdateGameCommand(game().getName(), game().getRules());
  }
}
