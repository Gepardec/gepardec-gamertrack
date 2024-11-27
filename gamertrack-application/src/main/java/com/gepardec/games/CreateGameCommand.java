package com.gepardec.games;

import jakarta.validation.constraints.NotBlank;

public record CreateGameCommand(@NotBlank String title, String rules) {

}
