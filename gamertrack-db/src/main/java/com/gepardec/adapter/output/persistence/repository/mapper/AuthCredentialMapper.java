package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.AuthCredentialEntity;
import com.gepardec.model.AuthCredential;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthCredentialMapper {
    public AuthCredentialEntity authCredentialModelToAuthCredentialEntity(AuthCredential authCredential) {
        return new AuthCredentialEntity(authCredential.getUsername(), authCredential.getPassword(), authCredential.getSalt(), authCredential.getToken());
    }

    public AuthCredential authCredentialEntityToAuthCredentialModel(AuthCredentialEntity authCredentialEntity) {
        return new AuthCredential(authCredentialEntity.getId(), authCredentialEntity.getToken(), authCredentialEntity.getUsername(),
                authCredentialEntity.getPassword(),  authCredentialEntity.getSalt());
    }
}
