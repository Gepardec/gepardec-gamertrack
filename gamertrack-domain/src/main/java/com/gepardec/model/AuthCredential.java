package com.gepardec.model;

public class AuthCredential {
    private Long id;
    private String username;
    private String password;
    private String token;
    private String salt;


    public AuthCredential(Long id, String token, String username, String password, String salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.token = token;
        this.salt = salt;

    }

    public AuthCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthCredential() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
