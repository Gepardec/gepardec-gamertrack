package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTournamentCommand(@NotBlank String name,
                                      @NotBlank String gameToken,
                                      @NotNull @Size(min = 2) List<String> participantTokens) {

}
