package com.gepardec.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity()
@Table(name = "users")
public class User extends AbstractEntity{

    @NotEmpty(message = "Firstname must be set")
    public String firstname;
    @NotEmpty(message = "Lastname must be set")
    public String lastname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    public List<Score> scores;

    public boolean deactivated;

    public User(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }
    //temp
    public User(String firstname, String lastname, boolean deactivated) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
    }

    public User(long id, String firstname, String lastname, boolean deactivated) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
    }

    public User() {}

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
