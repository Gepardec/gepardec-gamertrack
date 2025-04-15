package com.gepardec.rest.model.command;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateMatchCommand(@NotNull Game game, @NotNull List<User> users) {

}
