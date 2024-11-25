package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@SessionScoped
public class UserRepositoryImpl implements UserRepository, Serializable {

    @Produces
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<User> saveUser(User user) {
        entityManager.persist(user);
        User userSaved = entityManager.find(User.class, user.getId());

        return  Optional.ofNullable(userSaved);
    }

    @Override
    public Optional<User> updateUser(User user) {
        entityManager.merge(user);
        User userMerged = entityManager.find(User.class, user.getId());

        return  Optional.ofNullable(userMerged);
    }

    @Override
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    @Override
    public Optional<List<User>> findAllUsers() {
        return Optional.ofNullable(entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList());
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id)
                .getResultList().getFirst());
    }
}
