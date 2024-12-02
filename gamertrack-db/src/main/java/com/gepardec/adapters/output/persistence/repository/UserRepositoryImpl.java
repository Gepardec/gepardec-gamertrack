package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, Serializable {

    @PersistenceContext()
    protected EntityManager entityManager;

    @Override
    public Optional<User> saveUser(User user) {
        entityManager.persist(user);
        User userSaved = entityManager.find(User.class, user.getId());

        return  Optional.ofNullable(userSaved);
    }


    public Optional<User> updateUser(Long id, User user) {
        entityManager.merge(user);
        User userMerged = entityManager.find(User.class, user.getId());

        return  Optional.ofNullable(userMerged);
    }

    @Override
    public void deleteUser(User user) {
        entityManager.remove(user);
    }

    @Override
    public List<User> findAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id)
                .getResultList().getFirst());
    }

    @Override
    public Optional<User> findUserReferencesById(Long userId) {
        return Optional.of(entityManager.getReference(User.class, userId));
    }
}
