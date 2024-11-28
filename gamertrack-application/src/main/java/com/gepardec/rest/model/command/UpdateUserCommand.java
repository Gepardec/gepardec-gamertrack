package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserCommand(@NotBlank String firstname, String lastname){
}
