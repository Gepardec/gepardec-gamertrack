package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.TestFixtures;
import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jboss.arquillian.container.test.api.Deployment;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ArquillianExtension.class)
public class UserRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserRepository userRepository;

    @Inject
    Mapper mapper;

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(UserRepositoryImpl.class, UserRepository.class, UserDto.class, User.class, TestFixtures.class, EntityManager.class, Mapper.class)
                .addPackage(User.class.getPackage())
                .addPackage(UserRepository.class.getPackage())
                .addPackage(UserRepositoryImpl.class.getPackage())
                .addPackage(UserDto.class.getPackage())
                .addAsManifestResource("beans.xml")
                .addAsManifestResource("persistence.xml");
    }

    @Test
    void ensureSaveUserWorks(){
        UserDto user = new UserDto(TestFixtures.user(1L));
        userRepository.saveUser(user);
        assertTrue(userRepository.findUserById(1L).isPresent());
    }

}
