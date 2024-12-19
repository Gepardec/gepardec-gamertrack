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
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.jboss.arquillian.container.test.api.Deployment;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;

import javax.swing.text.html.Option;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ArquillianExtension.class)
public class UserRepositoryTest extends GamertrackDbIT{
    /*

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-----Using SavedId only temporary. Soon will be replaced with uuid/Base58-------
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------

     */
    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserRepository userRepository;

    @Inject
    Mapper mapper;

    @BeforeEach
    public void before() {
        userRepository.deleteAllUsers();
        entityManager.clear();
    }

    @Test
    void ensureSaveUserWorks(){
        UserDto user = new UserDto(TestFixtures.user(1L));
        Long savedId = userRepository.saveUser(user).get().getId();
        assertTrue(userRepository.findUserById(savedId).isPresent());
    }

    @Test
    void ensureUpdateUserWorks(){
        UserDto user = new UserDto(1L,"OLD","USER",false);
        Long savedId = userRepository.saveUser(user).get().getId();

        UserDto updatedUserDto = new UserDto(savedId,"NEW","USER",false);

        userRepository.updateUser(updatedUserDto);

        Optional<User> foundUser = userRepository.findUserById(savedId);

        assertTrue(foundUser.isPresent());
        assertEquals(foundUser.get().getFirstname(),updatedUserDto.firstname());
    }

    @Test
    void ensureDeleteUserWorks(){
        UserDto user = new UserDto(TestFixtures.user(1L));
        userRepository.saveUser(user);

        int sizeBefore = userRepository.findAllUsers().size();

        userRepository.deleteUser(user);

        assertEquals(1, sizeBefore);
        assertEquals(0, userRepository.findAllUsers().size());
        assertFalse(userRepository.findUserById(1L).isPresent());
    }

    @Test
    void ensureDeleteAllUsersWorks(){
        List<User> users = TestFixtures.users(4);
        userRepository.saveUser(new UserDto(users.get(0)));
        userRepository.saveUser(new UserDto(users.get(1)));
        userRepository.saveUser(new UserDto(users.get(2)));
        userRepository.saveUser(new UserDto(users.get(3)));

        int sizeBefore = userRepository.findAllUsers().size();

        userRepository.deleteAllUsers();

        assertEquals(4, sizeBefore);
        assertEquals(0, userRepository.findAllUsers().size());
        assertFalse(userRepository.findUserById(1L).isPresent());
    }

    @Test
    void ensureFindAllUsers(){

        UserDto user1 = new UserDto(TestFixtures.user(1L));
        UserDto user2 = new UserDto(TestFixtures.user(2L));
        UserDto user3 = new UserDto(3L,"Test","Arbeit",true);

        userRepository.saveUser(user1);
        userRepository.saveUser(user2);
        userRepository.saveUser(user3);

        assertEquals(2, userRepository.findAllUsers().size());
    }

    @Test
    void ensureFindAllUsersIncludeDeleted(){

        UserDto user1 = new UserDto(TestFixtures.user(1L));
        UserDto user2 = new UserDto(TestFixtures.user(2L));
        UserDto user3 = new UserDto(3L,"Test","deleted",true);

        userRepository.saveUser(user1);
        userRepository.saveUser(user2);
        userRepository.saveUser(user3);

        assertEquals(3, userRepository.findAllUsersIncludeDeleted().size());
    }

    @Test
    void ensureFindUserByIdWorks(){

        UserDto user1 = new UserDto(TestFixtures.user(1L));
        UserDto user2 = new UserDto(TestFixtures.user(2L));
        UserDto user3 = new UserDto(TestFixtures.user(3L));

        Long savedId1 = userRepository.saveUser(user1).get().getId();
        Long savedId2 = userRepository.saveUser(user2).get().getId();
        Long savedId3 = userRepository.saveUser(user3).get().getId();

        assertEquals(3, userRepository.findAllUsers().size());
        assertTrue(userRepository.findUserById(savedId1).isPresent());
    }

    @Test
    void ensureFindUserByIdWorksIncludedDeleted(){

        User user1 = new User(1L,"Max","Muster",false);
        User user2 = new User(2L,"Max","Muster",false);
        User user3 = new User(3L,"test","deleted",true);

        UserDto userDto1 = new UserDto(user1);
        UserDto userDto2 = new UserDto(user2);
        UserDto userDto3 = new UserDto(user3);

        Long savedId1 = userRepository.saveUser(userDto1).get().getId();
        Long savedId2 = userRepository.saveUser(userDto2).get().getId();
        Long savedId3 = userRepository.saveUser(userDto3).get().getId();

        assertTrue(userRepository.findUserByIdIncludeDeleted(savedId3).isPresent());
        assertTrue(userRepository.findUserById(savedId3).isEmpty());
    }

    @Test
    void ensureExistsByUserIdWorks(){

        List<User> users = TestFixtures.users(4);
        Long savedId1 = userRepository.saveUser(new UserDto(users.get(0))).get().getId();
        Long savedId2 = userRepository.saveUser(new UserDto(users.get(1))).get().getId();
        Long savedId3 = userRepository.saveUser(new UserDto(users.get(2))).get().getId();
        Long savedId4 = userRepository.saveUser(new UserDto(users.get(3))).get().getId();

        assertTrue(userRepository.existsByUserId(List.of(savedId1,savedId2,savedId3,savedId4)));
        assertFalse(userRepository.existsByUserId(List.of(1000L,1001L)));



    }

}
