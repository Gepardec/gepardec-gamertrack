package com.gepardec.rest.model.command;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//Only temporary for testing

public record CreateScoreCommand (@NotNull User user, @NotNull Game game, @NotNull double scorePoints) {
}
