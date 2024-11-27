package com.gepardec.service;

import com.gepardec.interfaces.services.UserService;
import jakarta.inject.Inject;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.SetBeanDiscoveryMode;
import org.junit.jupiter.api.extension.ExtendWith;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.UserService;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.SetBeanDiscoveryMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
/*
@ExtendWith(WeldJunit5Extension.class)
@AddBeanClasses({UserService.class, UserRepository.class})
@SetBeanDiscoveryMode(BeanDiscoveryMode.ALL)
public class UserServiceImplTest {

    //@Inject
    //UserServiceImpl userService;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(UserServiceImpl.class).addBeans(createRepoBean()).build();

    static Bean<?> createRepoBean() {
        return MockBean.builder()
                .types(UserRepository.class)
                .scope(ApplicationScoped.class)
                .creating(
                        // Mock object provided by Mockito
                        Mockito.mock(UserRepository.class))
                .build();
    }

    @Test
    public void createUserWorksCorrectly(UserServiceImpl userService) {
        User user = new User();
        user.firstname = "John";
        user.lastname = "Doe";

        userService.saveUser(user);

        //assertEquals(1, userService.findAllUsers().stream().toList().size());
        //Optional<User> test1 = userService.findUserById(1);
        Optional<List<User>> test2 = userService.findAllUsers();
        assertNotNull(userService.findUserById(1));
        assertEquals(userService.findUserById(1), Optional.empty());
        //assertEquals("John", userService.findUserById(1).get().firstname);
        //assertEquals(1,2);
    }
}


 */

/*
@ExtendWith(ArquillianExtension.class)
@AddBeanClasses({UserService.class, UserRepository.class})
@SetBeanDiscoveryMode(BeanDiscoveryMode.ALL)
public class UserServiceImplTest {

    @Inject
    UserServiceImpl userService;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(User.class.getPackage())
                .addClass(UserService.class)
                .addAsResource("persistence.xml", "META-INF/persistence.xml");
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void createUserWorksCorrectly(UserServiceImpl userService) {
        User user = new User();
        user.firstname = "John";
        user.lastname = "Doe";

        userService.saveUser(user);

        //assertEquals(1, userService.findAllUsers().stream().toList().size());
        //Optional<User> test1 = userService.findUserById(1);
        Optional<List<User>> test2 = userService.findAllUsers();
        assertNotNull(userService.findUserById(1));
        assertEquals(userService.findUserById(1), Optional.empty());
        //assertEquals("John", userService.findUserById(1).get().firstname);
        //assertEquals(1,2);
    }
}


 */