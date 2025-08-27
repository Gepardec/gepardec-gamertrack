package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.ScoreRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Score;
import com.gepardec.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    GameRepository gameRepository;
    @Mock
    ScoreRepository scoreRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    ScoreServiceImpl scoreService;

    @Mock
    TokenService tokenService;

    @Mock
    GameServiceImpl gameService;

    @Test
    void ensureSaveAndReadGameWorksAndReturnsUser() {
        User user = TestFixtures.user(1L);

        when(userRepository.saveUser(user)).thenReturn(Optional.of(user));
        assertEquals(userService.saveUser(user).get().getFirstname(), user.getFirstname());
    }

    @Test
    void ensureUpdatingExistingUserWorksAndReturnsUser() {
        User userEdit = TestFixtures.user(1L);

        //User was found
        when(userRepository.findUserByToken(userEdit.getToken())).thenReturn(Optional.of(userEdit));

        when(userRepository.updateUser(userEdit)).thenReturn(Optional.of(userEdit));

        Optional<User> updatedUser = userService.updateUser(userEdit);

        assertTrue(updatedUser.isPresent());
        assertEquals(userEdit.getFirstname(), updatedUser.get().getFirstname());
        assertEquals(userEdit.getLastname(), updatedUser.get().getLastname());


    }
    @Test
    void ensureUpdatingNonExistingUserWorksAndReturnsEmpty() {
        User userEdit = TestFixtures.user(1L);

        //User was not found
        when(userRepository.findUserByToken(userEdit.getToken())).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUser(userEdit);

        assertFalse(updatedUser.isPresent());

    }
    @Test
    void ensureDeletingNonExistingUserWorksAndReturnsEmpty() {
        User user = TestFixtures.user(1L);

        //User was not found
        when(userRepository.findUserByToken(user.getToken())).thenReturn(Optional.empty());

        Optional<User> deletedUser = userService.deleteUser(user.getToken());

        assertFalse(deletedUser.isPresent());

    }
    @Test
    void ensureDeletingUserWithNoScoresWorksReturnsDeletedUser() {
        User user = TestFixtures.user(1L);

        when(userRepository.findUserByToken(user.getToken())).thenReturn(Optional.of(user));
        when(scoreService.findScoresByUser(user.getToken(),true)).thenReturn(List.of());

        Optional<User> deletedUser = userService.deleteUser(user.getToken());
        assertTrue(deletedUser.isPresent());
        assertEquals(user.getFirstname(), deletedUser.get().getFirstname());

    }
    @Test
    void ensureDeletingUserWithScoresWorksReturnsDeletedUser() {
        User user = TestFixtures.user(1L);
        Score score = TestFixtures.score(1L,1L,1L);

        when(userRepository.findUserByToken(user.getToken())).thenReturn(Optional.of(user));
        when(scoreService.findScoresByUser(user.getToken(),true)).thenReturn(List.of(score));

        Optional<User> deletedUser = userService.deleteUser(user.getToken());
        assertTrue(deletedUser.isPresent());

    }
    @Test
    void ensureFindAllUsersWorksAndReturnsAllUsers() {
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);
        user3.setDeactivated(true);

        when(userRepository.findAllUsersSortedByMatchCount(false)).thenReturn(List.of(user1,user2));

        assertEquals(2, userService.findAllUsers(false).size());
    }
    @Test
    void ensureFindAllUsersIncludeDeletedWorksAndReturnsAllUsersIncludingDeleted() {
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);
        user3.setDeactivated(true);

        when(userRepository.findAllUsersSortedByMatchCount(true)).thenReturn(List.of(user1, user2, user3));

        assertEquals(3, userService.findAllUsers(true).size());
    }
    @Test
    void ensureFindUserByTokenWorks() {
        List<User> users = TestFixtures.users(3);
        when(userRepository.findUserByToken(users.get(1).getToken())).thenReturn(Optional.of(users.get(1)));

        users.get(1).setDeactivated(true);

        User foundUser = userService.findUserByToken(users.get(1).getToken()).get();

        assertEquals(foundUser.getFirstname(), users.get(1).getFirstname());
        assertEquals(foundUser.getLastname(), users.get(1).getLastname());
    }
    @Test
    void ensureFindUserByTokenIncludeDeletedWorks() {
        List<User> users = TestFixtures.users(3);
        users.get(1).setDeactivated(true);

        when(userRepository.findUserByToken(users.get(1).getToken())).thenReturn(Optional.of(users.get(1)));

        User foundUser = userService.findUserByToken(users.get(1).getToken()).get();

        assertEquals(foundUser.getFirstname(), users.get(1).getFirstname());
        assertEquals(foundUser.getLastname(), users.get(1).getLastname());
    }

}