package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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

    @NotNull(message = "ScorePoints must be set")
    private double scorePoints;

    @NotEmpty(message = "Token must be set")
    private String token;

    public ScoreEntity(UserEntity user, GameEntity game, double scorePoints, String token) {
        this.user = user;
        this.game = game;
        this.scorePoints = scorePoints;
        this.token = token;
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
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
