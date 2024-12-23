package com.gepardec.model;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Match {
    private Long id;
    private Game game;
    private List<User> users;

    public Match(Long id) {
        this.id = id;
    }

    public Match(Long id, Game game, List<User> users) {
        this.id = id;
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
}
