package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;

public record AuthCredentialCommand(@NotBlank String username, @NotBlank String password) {
}
