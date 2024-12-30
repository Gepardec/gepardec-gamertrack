package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;

public record CreateGameCommand(@NotBlank String name, String rules) {

}
