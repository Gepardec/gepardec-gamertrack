package com.gepardec.rest.model.command;

import jakarta.validation.constraints.NotBlank;

public record CreateUserCommand (@NotBlank String firstname, String lastname){
}
