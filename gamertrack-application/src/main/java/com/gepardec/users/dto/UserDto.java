package com.gepardec.users.dto;


import com.gepardec.model.User;
import jakarta.validation.constraints.NotEmpty;

public record UserDto (@NotEmpty String firstname, @NotEmpty String lastname) {
    public UserDto(User user){
        this(user.getFirstname(),user.getLastname());
    }
}