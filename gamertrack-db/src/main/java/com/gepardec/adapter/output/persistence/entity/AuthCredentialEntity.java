package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "credentials", indexes = @Index(name = "ux_credentials_token", columnList = "token", unique = true))
public class AuthCredentialEntity extends AbstractEntity {

    @Column(name = "token", unique = true)
    private String token;
    @NotBlank(message = "Username must be set")
    @Column(name = "username", unique = true)
    private String username;
    @NotBlank(message = "Password must be set")
    private String password;
    @NotBlank(message = "salt must be set")
    private String salt;

    public AuthCredentialEntity(String username, String password, String salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public AuthCredentialEntity() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
