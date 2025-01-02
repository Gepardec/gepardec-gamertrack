package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity()
@Table(name = "users")
public class UserEntity extends AbstractEntity{

    @NotEmpty(message = "Firstname must be set")
    private String firstname;
    @NotEmpty(message = "Lastname must be set")
    private String lastname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ScoreEntity> scores;

    @NotNull(message = "Deactivated must be set")
    private boolean deactivated;

    @NotEmpty(message = "Token must be set")
    private String token;

    public UserEntity(String firstname, String lastname, boolean deactivated, String token) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
        this.token = token;
    }

    public UserEntity(long id, String firstname, String lastname, boolean deactivated, String token) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
        this.token = token;
    }

    public UserEntity() {}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
