package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRestMapperTest {

  @InjectMocks
  UserRestMapper userRestMapper;


  @Test
  public void ensureMapCreateUserCommandDtoToUserDtoWorks() {
    CreateUserCommand userCommand = RestTestFixtures.createUserCommand(1L);

    User mappedUser = userRestMapper.createUserCommandtoUser(userCommand);

    assertEquals(userCommand.firstname(), mappedUser.getFirstname());
    assertEquals(userCommand.lastname(), mappedUser.getLastname());

  }

  @Test
  public void ensureMapUpdateUserCommandDtoToUserDtoWorks() {

    UpdateUserCommand updateUserCommand = RestTestFixtures.updateUserCommand(1L);

    User mappedUser = userRestMapper.updateUserCommandtoUser("n30asij-sck2kcj3", updateUserCommand);

    assertEquals("n30asij-sck2kcj3", mappedUser.getToken());
    assertEquals(updateUserCommand.firstname(), mappedUser.getFirstname());
    assertEquals(updateUserCommand.lastname(), mappedUser.getLastname());
    assertEquals(updateUserCommand.deactivated(), mappedUser.isDeactivated());

  }
}