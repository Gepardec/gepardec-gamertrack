package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//Only temporary for testing

public record CreateScoreCommand (@NotNull long userId, @NotNull long gameId, @NotNull double scorePoints) {
}
