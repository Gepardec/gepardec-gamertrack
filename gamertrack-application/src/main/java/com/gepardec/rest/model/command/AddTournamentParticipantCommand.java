package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;

public record AddTournamentParticipantCommand(@NotBlank String userToken) {

}
