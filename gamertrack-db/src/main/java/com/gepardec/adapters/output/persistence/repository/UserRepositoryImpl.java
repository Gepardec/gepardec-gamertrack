package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, Serializable {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    @PersistenceContext()
    protected EntityManager entityManager;


    @Override
    public List<User> findAllUsersIncludeDeleted() {
        List<User> resultList = entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
        log.info("Find all users including deleted user. Returned list of size:{}", resultList.size());

        return resultList;
    }
    @Override
    public List<User> findAllUsers() {
        List<User> resultList = entityManager.createQuery("SELECT u FROM User u Where u.firstname <> 'DELETED' And u.lastname <> 'U$ER' ", User.class)
                .getResultList();
        log.info("Find all users. Returned list of size:{}", resultList.size());
        return resultList;

    }

    @Override
    public Optional<User> findUserById(long id) {
        List<User> resultList = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id)
                .getResultList();
        log.info("Find user with id: {}. Returned list of size:{}", id, resultList.size());
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.getFirst());
    }
    @Override
    public Optional<User> saveUser(User user) {
        entityManager.persist(user);
        User userSaved = entityManager.find(User.class, user.getId());
        log.info("Saved user with id: {}", userSaved.getId());
        return  Optional.ofNullable(userSaved);
    }


    @Override
    public void deleteUser(User user) {
        log.info("Deleted user with id: {}", user.getId());
        entityManager.remove(user);
    }
}
