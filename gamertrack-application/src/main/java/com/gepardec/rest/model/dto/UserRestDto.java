package com.gepardec.rest.model.dto;


import com.gepardec.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRestDto(@NotNull Long id, @NotBlank String firstname, @NotBlank String lastname, boolean deactivated ) {
    public UserRestDto(User user){
        this(user.getId(),user.getFirstname(),user.getLastname(),user.isDeactivated());
    }
}