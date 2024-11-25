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

    public List<Score> scores;

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

    @OneToMany
    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}
