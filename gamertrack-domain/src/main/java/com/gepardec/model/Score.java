package com.gepardec.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Score extends AbstractEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    public Game game;
    public double score;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
