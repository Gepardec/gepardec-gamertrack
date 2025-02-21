package com.gepardec.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public class Match {

    private Long id;
    private String token;
    @NotNull(message = "Game must not be null")
    private Game game;
    @NotEmpty(message = "User List must not be null or Empty")
    private List<User> users;

    public Match() {
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
