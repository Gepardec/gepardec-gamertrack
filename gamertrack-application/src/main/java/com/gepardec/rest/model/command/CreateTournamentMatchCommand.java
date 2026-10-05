package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Users of the match in result order: the first user is the winner,
 * as with regular matches.
 */
public record CreateTournamentMatchCommand(@NotNull @Size(min = 2) List<String> userTokens) {

}
