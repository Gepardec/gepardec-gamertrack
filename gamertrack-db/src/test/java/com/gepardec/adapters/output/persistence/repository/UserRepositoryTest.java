package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.SetBeanDiscoveryMode;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldJunit5AutoExtension.class)
@AddBeanClasses({UserRepository.class, UserRepositoryImpl.class, EntityManager.class})
@SetBeanDiscoveryMode(BeanDiscoveryMode.ALL)
public class UserRepositoryTest {

    @Inject
    UserRepository userRepository;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("integration-test");

    @PersistenceContext
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    @BeforeEach
    public void init() {

    }

    @Test
    public void ensureCreateUserWorksCorrectly() {
        User user = new User();
        user.firstname = "John";
        user.lastname = "Doe";

        entityManager.persist(user);
        //userRepository.saveUser(user);

        //assertEquals(1, userRepository.findAllUsers().get().size());
    }
}
