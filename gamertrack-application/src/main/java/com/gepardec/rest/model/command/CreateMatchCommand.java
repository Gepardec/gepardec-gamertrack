package com.gepardec.rest.model.command;

import com.gepardec.model.Game;
import com.gepardec.model.User;

import java.util.List;

public record CreateMatchCommand(Game game, List<User> users) {

}
