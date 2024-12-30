package com.gepardec.rest.model.dto;


import com.gepardec.model.User;

public record UserRestDto(Long id, String firstname, String lastname, boolean deactivated ) {
    public UserRestDto(User user){
        this(user.getId(),user.getFirstname(),user.getLastname(),user.isDeactivated());
    }
}