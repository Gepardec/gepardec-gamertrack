package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        User user = TestFixtures.user(1L);

        when(userRepository.saveUser(user)).thenReturn(Optional.of(user));
        assertEquals(userService.saveUser(user).get().getFirstname(), user.getFirstname());
    }

    @Test
    void ensureUpdatingExistingUserWorksAndReturnsUser() {
        User userEdit = TestFixtures.user(1L);

        //User was found
        when(userRepository.findUserById(userEdit.getId())).thenReturn(Optional.of(userEdit));

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
        when(userRepository.findUserById(userEdit.getId())).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUser(userEdit);

        assertFalse(updatedUser.isPresent());

    }
    @Test
    void ensureDeletingNonExistingUserWorksAndReturnsEmpty() {
        User user = TestFixtures.user(1L);

        //User was not found
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.empty());

        Optional<User> deletedUser = userService.deleteUser(user.getId());

        assertFalse(deletedUser.isPresent());

    }
    @Test
    void ensureDeletingUserWithNoScoresWorksReturnsDeletedUser() {
        User user = TestFixtures.user(1L);

        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(scoreService.findScoresByUser(user.getId(),true)).thenReturn(List.of());

        Optional<User> deletedUser = userService.deleteUser(user.getId());
        assertTrue(deletedUser.isPresent());
        assertEquals(user.getFirstname(), deletedUser.get().getFirstname());

    }
    @Test
    void ensureDeletingUserWithScoresWorksReturnsDeletedUser() {
        User user = TestFixtures.user(1L);
        Score score = TestFixtures.score(1L,1L,1L);

        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(scoreService.findScoresByUser(user.getId(),true)).thenReturn(List.of(score));

        Optional<User> deletedUser = userService.deleteUser(user.getId());
        assertTrue(deletedUser.isPresent());

    }
    @Test
    void ensureFindAllUsersWorksAndReturnsAllUsers() {
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);
        user3.setDeactivated(true);

        when(userRepository.findAllUsers()).thenReturn(List.of(user1,user2));

        assertEquals(2, userService.findAllUsers().size());
    }
    @Test
    void ensureFindAllUsersIncludeDeletedWorksAndReturnsAllUsersIncludingDeleted() {
        User user1 = TestFixtures.user(1L);
        User user2 = TestFixtures.user(2L);
        User user3 = TestFixtures.user(3L);
        user3.setDeactivated(true);

        when(userRepository.findAllUsersIncludeDeleted()).thenReturn(List.of(user1, user2, user3));

        assertEquals(3, userService.findAllUsersIncludeDeleted().size());
    }
    @Test
    void ensureFindUserByIdWorks() {
        List<User> users = TestFixtures.users(3);
        when(userRepository.findUserById(2)).thenReturn(Optional.of(users.get(1)));

        User foundUser = userService.findUserById(2).get();

        assertEquals(foundUser.getFirstname(), users.get(1).getFirstname());
        assertEquals(foundUser.getLastname(), users.get(1).getLastname());
    }
    @Test
    void ensureFindUserByIdIncludeDeletedWorks() {
        List<User> users = TestFixtures.users(3);

        when(userRepository.findUserByIdIncludeDeleted(2)).thenReturn(Optional.of(users.get(1)));

        User foundUser = userService.findUserByIdIncludeDeleted(2).get();

        assertEquals(foundUser.getFirstname(), users.get(1).getFirstname());
        assertEquals(foundUser.getLastname(), users.get(1).getLastname());
    }

}