package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "scores", indexes = @Index(name = "ux_scores_token", columnList = "token", unique = true))
public class ScoreEntity extends AbstractEntity {

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
  @NotNull
  private boolean defaultScore;

  public ScoreEntity(UserEntity user, GameEntity game, double scorePoints, String token, boolean defaultScore) {
    this.user = user;
    this.game = game;
    this.scorePoints = scorePoints;
    this.token = token;
    this.defaultScore = defaultScore;
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

  public boolean isDefaultScore() {
    return defaultScore;
  }
  public void setDefaultScore(boolean defaultScore) {
    this.defaultScore = defaultScore;
  }
}
