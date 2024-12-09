package com.gepardec.rest.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import com.gepardec.rest.model.mapper.RestMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class RestMapperTest {

    @InjectMocks
    RestMapper mapper = new RestMapper();

    @Test
    public void ensureMapCreateScoreCommandToScoreDtoWorks() {
        CreateScoreCommand scoreCommand = RestTestFixtures.createScoreCommand(1L);

        ScoreDto mappedScoreDto = mapper.toScore(scoreCommand);

        assertEquals(scoreCommand.userId(),mappedScoreDto.userId());
        assertEquals(scoreCommand.gameId(),mappedScoreDto.gameId());
        assertEquals(scoreCommand.scorePoints(),mappedScoreDto.scorePoints());

    }

    @Test
    public void ensureMapCreateUserCommandDtoToUserDtoWorks() {
        CreateUserCommand userCommand = RestTestFixtures.createUserCommand(1L);

        UserDto mappedUserDto = mapper.CreateUserCommandtoUser(userCommand);

        assertEquals(userCommand.firstname(),mappedUserDto.firstname());
        assertEquals(userCommand.lastname(),mappedUserDto.lastname());

    }

    @Test
    public void ensureMapUpdateUserCommandDtoToUserDtoWorks() {

        UpdateUserCommand updateUserCommand = RestTestFixtures.updateUserCommand(1L);

        UserDto mappedUserDto = mapper.UpdateUserCommandtoUser(1L,updateUserCommand);

        assertEquals(1L, mappedUserDto.id());
        assertEquals(updateUserCommand.firstname(),mappedUserDto.firstname());
        assertEquals(updateUserCommand.lastname(),mappedUserDto.lastname());
        assertEquals(updateUserCommand.deactivated(),mappedUserDto.deactivated());

    }
}
