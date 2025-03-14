package com.gepardec.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Match {

    private Long id;
    private String token;
    @NotNull(message = "Game must not be null")
    private Game game;
    @NotEmpty(message = "User List must not be null or Empty")
    private List<User> users;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public Match() {
    }

    public Match(Long id, String token, LocalDateTime createdOn, LocalDateTime updatedOn, Game game, List<User> users) {
        this.id = id;
        this.token = token;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.game = game;
        this.users = users;
    }

    public Match(Long id, String token, Game game, List<User> users) {
        this.id = id;
        this.token = token;
        this.game = game;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", key='" + token + '\'' +
                ", game=" + game +
                ", users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id) && Objects.equals(token, match.token) && Objects.equals(game, match.game) && Objects.equals(users, match.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, game, users);
    }
}
