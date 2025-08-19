package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.UserEntity;
import com.gepardec.impl.service.TokenServiceImpl;
import com.gepardec.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gepardec.TestFixtures.user;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserMapperTest {

  @InjectMocks
  UserMapper userMapper = new UserMapper();
  @InjectMocks
  TokenServiceImpl tokenService;

  @Test
  public void ensureUserModelToUserEntityMappingWorks() {
    User user = user(1L);

    UserEntity mappedUser = userMapper.userModelToUserEntity(user);

    assertEquals(mappedUser.getFirstname(), user.getFirstname());
    assertEquals(mappedUser.getLastname(), user.getLastname());
    assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
  }

  @Test
  public void ensureUserModelToExistingUserEntityMappingWorks() {
    User user = user(1L);

    UserEntity existingUser = new UserEntity(3, "firstname", "lastname", false,user.getToken());

    UserEntity mappedUser = userMapper.userModeltoExistingUserEntity(user, existingUser);

    assertEquals(existingUser.getId(), mappedUser.getId());
    assertEquals(existingUser.getToken(), mappedUser.getToken());
    assertEquals(existingUser.getFirstname(), mappedUser.getFirstname());
    assertEquals(existingUser.getLastname(), mappedUser.getLastname());
    assertEquals(existingUser.isDeactivated(), mappedUser.isDeactivated());
  }


}
