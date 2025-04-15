package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.AuthCredentialEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.AuthCredentialMapper;
import com.gepardec.core.repository.AuthRepository;
import com.gepardec.model.AuthCredential;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class AuthRepositoryImpl implements AuthRepository, Serializable {

    private static final Logger log = LoggerFactory.getLogger(AuthRepositoryImpl.class);

    @Inject
    protected EntityManager entityManager;
    @Inject
    private AuthCredentialMapper authCredentialMapper;

    @Override
    public Optional<AuthCredential> findByUsername(String username) {
        List<AuthCredentialEntity> resultList = entityManager.createQuery(
                        "SELECT ac FROM AuthCredentialEntity ac " +
                                "WHERE ac.username = :username ", AuthCredentialEntity.class)
                .setParameter("username", username)
                .getResultList();

        return resultList.isEmpty()
                ? Optional.empty()
                : Optional.of(authCredentialMapper.authCredentialEntityToAuthCredentialModel(resultList.getFirst()));
    }

    @Override
    public Optional<AuthCredential> createDefaultUser(AuthCredential authCredential){
        AuthCredentialEntity authCredentialEntity = authCredentialMapper.authCredentialModelToAuthCredentialEntity(authCredential);
        entityManager.persist(authCredentialEntity);

        return entityManager.find(AuthCredentialEntity.class, authCredentialEntity.getId()) != null
        ? Optional.of(authCredentialMapper.authCredentialEntityToAuthCredentialModel(authCredentialEntity))
        : Optional.empty();
    }


    @Override
    public void deleteExistingAuthUsers() {
        entityManager.createQuery("DELETE FROM AuthCredentialEntity").executeUpdate();
    }
}
