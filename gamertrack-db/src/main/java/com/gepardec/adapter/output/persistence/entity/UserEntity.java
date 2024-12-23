package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

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

    private boolean deactivated;

    public UserEntity(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }
    //temp
    public UserEntity(String firstname, String lastname, boolean deactivated) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
    }

    public UserEntity(long id, String firstname, String lastname, boolean deactivated) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
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
}
