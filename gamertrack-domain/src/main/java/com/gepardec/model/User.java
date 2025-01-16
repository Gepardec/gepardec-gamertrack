package com.gepardec.model;

public class User{
    private Long id;
    private  String firstname;
    private  String lastname;
    private boolean deactivated;

    public User() {
    }

    public User(Long id, String firstname, String lastname, boolean deactivated) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.deactivated = deactivated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

