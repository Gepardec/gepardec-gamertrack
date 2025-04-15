package com.gepardec.model;

import java.util.Objects;

public class User {

  private Long id;
  private String firstname;
  private String lastname;
  private boolean deactivated;
  private String token;


  public User() {
  }

  public User(Long id, String firstname, String lastname, boolean deactivated, String token) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.deactivated = deactivated;
    this.token = token;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return deactivated == user.deactivated && Objects.equals(id, user.id)
        && Objects.equals(firstname, user.firstname) && Objects.equals(lastname,
        user.lastname) && Objects.equals(token, user.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstname, lastname, deactivated, token);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", firstname='" + firstname + '\'' +
        ", lastname='" + lastname + '\'' +
        ", deactivated=" + deactivated +
        ", token='" + token + '\'' +
        '}';
  }
}

