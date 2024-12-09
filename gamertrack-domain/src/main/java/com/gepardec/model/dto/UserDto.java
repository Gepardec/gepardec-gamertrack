package com.gepardec.model.dto;

import com.gepardec.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserDto(@NotNull long id, @NotEmpty String firstname, @NotEmpty String lastname, boolean deactivated) {
    public UserDto(User user){
        this(user.getId(), user.getFirstname(),user.getLastname(),user.isDeactivated());
    }

}

