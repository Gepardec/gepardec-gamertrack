package com.gepardec.rest.model.command;

import com.gepardec.model.TournamentState;
import jakarta.validation.constraints.NotNull;

public record UpdateTournamentStateCommand(@NotNull TournamentState state) {

}
