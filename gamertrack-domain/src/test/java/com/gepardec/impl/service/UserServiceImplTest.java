package com.gepardec.impl.service;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.UserDto;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    ScoreServiceImpl scoreService;

    @Test
    void ensureSaveAndReadGameWorksAndReturnsUser() {
        User user = new User("Max","Muster");
        user.setId(1L);
        UserDto userDto = new UserDto(user);

        when(userRepository.saveUser(userDto)).thenReturn(Optional.of(user));
        assertEquals(userService.saveUser(userDto).get().getFirstname(), user.getFirstname());
    }

    @Test
    void ensureUpdatingExistingUserWorksAndReturnsUser() {
        User userEdit = new User("Phillip","Muster");
        userEdit.setId(1L);
        UserDto userDto = new UserDto(userEdit);

        //User was found
        when(userRepository.findUserById(userEdit.getId())).thenReturn(Optional.of(userEdit));

        when(userRepository.updateUser(userDto)).thenReturn(Optional.of(userEdit));

        Optional<User> updatedUser = userService.updateUser(userDto);

        assertTrue(updatedUser.isPresent());
        assertEquals(userEdit.getFirstname(), updatedUser.get().getFirstname());
        assertEquals(userEdit.getLastname(), updatedUser.get().getLastname());


    }
    @Test
    void ensureUpdatingNonExistingUserWorksAndReturnsEmpty() {
        User userEdit = new User("Phillip","Muster");
        userEdit.setId(1L);
        UserDto userDto = new UserDto(userEdit);

        //User was not found
        when(userRepository.findUserById(userEdit.getId())).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUser(userDto);

        assertFalse(updatedUser.isPresent());

    }
    @Test
    void ensureDeletingNonExistingUserWorksAndReturnsEmpty() {
        User userEdit = new User("Phillip","Muster");
        userEdit.setId(1L);

        //User was not found
        when(userRepository.findUserById(userEdit.getId())).thenReturn(Optional.empty());

        Optional<User> deletedUser = userService.deleteUser(userEdit.getId());

        assertFalse(deletedUser.isPresent());

    }
    @Test
    void ensureDeletingUserWithNoScoresWorksReturnsDeletedUser() {
        User user = new User("Phillip","Muster");
        user.setId(1L);
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(scoreService.findScoreByUser(user.getId())).thenReturn(List.of());

        Optional<User> deletedUser = userService.deleteUser(user.getId());
        assertTrue(deletedUser.isPresent());
        assertEquals(user.getFirstname(), deletedUser.get().getFirstname());

    }
    @Test
    void ensureDeletingUserWithScoresWorksReturnsDeletedUser() {
        User user = new User("Phillip","Muster");
        user.setId(1L);
        Score score = new Score();
        score.setId(1L);
        score.setUser(user);
        score.setGame(new Game("Vier Gewinnt", "nicht schummeln"));

        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(scoreService.findScoreByUser(user.getId())).thenReturn(List.of(score));

        Optional<User> deletedUser = userService.deleteUser(user.getId());
        assertTrue(deletedUser.isPresent());
        assertEquals("DELETED", deletedUser.get().getFirstname());
        assertEquals("U$ER", deletedUser.get().getLastname());
    }
    @Test
    void ensureFindAllUsersWorksAndReturnsAllUsers() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("DELETED","U$ER");

        when(userRepository.findAllUsers()).thenReturn(List.of(user1,user2));

        assertEquals(2, userService.findAllUsers().size());
    }
    @Test
    void ensureFindAllUsersIncludeDeletedWorksAndReturnsAllUsersIncludingDeleted() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("DELETED","U$ER");

        when(userRepository.findAllUsersIncludeDeleted()).thenReturn(List.of(user1, user2, user3));

        assertEquals(3, userService.findAllUsersIncludeDeleted().size());
    }
    @Test
    void ensureFindUserByIdWorks() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("DELETED","U$ER");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        when(userRepository.findUserById(2)).thenReturn(Optional.of(user2));

        User foundUser = userService.findUserById(2).get();

        assertEquals(foundUser.getFirstname(), user2.getFirstname());
        assertEquals(foundUser.getLastname(), user2.getLastname());
    }
    @Test
    void ensureFindUserByIdIncludeDeletedWorks() {
        User user1 = new User("Max","Muster");
        User user2 = new User("Paul","Meyer");
        User user3 = new User("DELETED","U$ER");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        when(userRepository.findUserByIdIncludeDeleted(3)).thenReturn(Optional.of(user3));

        User foundUser = userService.findUserByIdIncludeDeleted(3).get();

        assertEquals(foundUser.getFirstname(), user3.getFirstname());
        assertEquals(foundUser.getLastname(), user3.getLastname());
    }

}