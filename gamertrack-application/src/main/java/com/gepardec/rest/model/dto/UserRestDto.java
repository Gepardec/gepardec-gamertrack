package com.gepardec.rest.model.dto;


import com.gepardec.model.User;
import jakarta.validation.constraints.NotEmpty;

public record UserRestDto(@NotEmpty String firstname, @NotEmpty String lastname) {
    public UserRestDto(User user){
        this(user.getFirstname(),user.getLastname());
    }
}