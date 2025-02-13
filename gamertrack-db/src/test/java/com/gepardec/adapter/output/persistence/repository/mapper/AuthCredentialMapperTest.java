package com.gepardec.adapter.output.persistence.repository.mapper;

import com.gepardec.adapter.output.persistence.entity.AuthCredentialEntity;
import com.gepardec.impl.service.TokenServiceImpl;
import com.gepardec.model.AuthCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthCredentialMapperTest {

    @InjectMocks
    AuthCredentialMapper mapper = new AuthCredentialMapper();
    @InjectMocks
    TokenServiceImpl tokenService;

    @Test
    public void ensureAuthCredentialModelToAuthCredentialEntityMappingWorks() {
        AuthCredential authCredential = new AuthCredential(tokenService.generateToken(),"admin",
                "password","sdfvwefvcw");

        AuthCredentialEntity mappedAuthCredentialEntity = mapper.authCredentialModelToAuthCredentialEntity(authCredential);

        assertEquals(mappedAuthCredentialEntity.getToken(), authCredential.getToken());
        assertEquals(mappedAuthCredentialEntity.getUsername(), authCredential.getUsername());
        assertEquals(mappedAuthCredentialEntity.getPassword(), authCredential.getPassword());
        assertEquals(mappedAuthCredentialEntity.getSalt(), authCredential.getSalt());
    }

    @Test
    public void ensureAuthCredentialEntityToAuthCredentialModelMappingWorks() {
        AuthCredentialEntity authCredentialEntity = new AuthCredentialEntity("admin",
                "password","sdfvwefvcw",tokenService.generateToken());


        AuthCredential mappedAuthCredential = mapper.authCredentialEntityToAuthCredentialModel(authCredentialEntity);


        assertEquals(mappedAuthCredential.getId(), authCredentialEntity.getId());
        assertEquals(mappedAuthCredential.getToken(), authCredentialEntity.getToken());
        assertEquals(mappedAuthCredential.getUsername(), authCredentialEntity.getUsername());
        assertEquals(mappedAuthCredential.getPassword(), authCredentialEntity.getPassword());
        assertEquals(mappedAuthCredential.getSalt(), authCredentialEntity.getSalt());
    }

}
