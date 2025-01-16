package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ScoreEntity extends AbstractEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "fk_game_score", foreignKey = @ForeignKey(name = "fk_game_score"))
    private GameEntity game;

    @NotNull
    private double scorePoints;

    public ScoreEntity(UserEntity user, GameEntity game, double scorePoints) {
        this.user = user;
        this.game = game;
        this.scorePoints = scorePoints;
    }

    public ScoreEntity() {

    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
    public double getScorePoints() {
        return scorePoints;
    }
    public void setScorePoints(double scorePoints) {
        this.scorePoints = scorePoints;
    }
}
