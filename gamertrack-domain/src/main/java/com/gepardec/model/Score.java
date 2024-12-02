package com.gepardec.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Score extends AbstractEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    public User user;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "fk_game", foreignKey = @ForeignKey(name = "fk_game"))
    public Game game;

    @NotNull
    public double score;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
