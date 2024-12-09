package com.gepardec.rest.model.mapper;

import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestMapper {
    public ScoreDto toScore(CreateScoreCommand scoreCommand) {
        return new ScoreDto(0,scoreCommand.userId(), scoreCommand.gameId(), scoreCommand.scorePoints());
    }
    public UserDto CreateUserCommandtoUser(CreateUserCommand createUserCommand) {
        return new UserDto(0,createUserCommand.firstname(), createUserCommand.lastname(), false);
    }
    public UserDto UpdateUserCommandtoUser(Long id, UpdateUserCommand updateUserCommand) {
        return new UserDto(id,updateUserCommand.firstname(), updateUserCommand.lastname(), updateUserCommand.deactivated());
    }

}
