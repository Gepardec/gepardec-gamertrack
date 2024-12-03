package com.gepardec.impl;

import com.gepardec.adapters.output.persistence.repository.UserRepositoryImpl;
import com.gepardec.model.User;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    EntityManager entityManager;

    @Mock
    UserRepositoryImpl userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void ensureSaveAndReadGameWorks() {
        User user = new User("Max","Muster");

        when(userRepository.saveUser(user)).thenReturn(Optional.of(user));
        assertEquals(userService.saveUser(user).get().getFirstname(), user.getFirstname());
    }
}