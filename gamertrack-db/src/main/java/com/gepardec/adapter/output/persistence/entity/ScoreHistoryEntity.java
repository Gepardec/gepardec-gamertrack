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
@Table(name = "score_history", indexes = {
    @Index(name = "ux_score_history_token", columnList = "token", unique = true),
    @Index(name = "ix_score_history_match_token", columnList = "matchToken")})
public class ScoreHistoryEntity extends AbstractEntity {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_user_score_history", foreignKey = @ForeignKey(name = "fk_user_score_history"))
  private UserEntity user;

  @NotNull
  @ManyToOne()
  @JoinColumn(name = "fk_game_score_history", foreignKey = @ForeignKey(name = "fk_game_score_history"))
  private GameEntity game;

  @NotEmpty(message = "MatchToken must be set")
  private String matchToken;

  @NotNull(message = "PreviousScorePoints must be set")
  private double previousScorePoints;

  @NotNull(message = "NewScorePoints must be set")
  private double newScorePoints;

  @NotNull(message = "ScoreChange must be set")
  private double scoreChange;

  @NotEmpty(message = "Token must be set")
  private String token;

  public ScoreHistoryEntity(UserEntity user, GameEntity game, String matchToken,
      double previousScorePoints, double newScorePoints, double scoreChange, String token) {
    this.user = user;
    this.game = game;
    this.matchToken = matchToken;
    this.previousScorePoints = previousScorePoints;
    this.newScorePoints = newScorePoints;
    this.scoreChange = scoreChange;
    this.token = token;
  }

  public ScoreHistoryEntity() {

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

  public String getMatchToken() {
    return matchToken;
  }

  public void setMatchToken(String matchToken) {
    this.matchToken = matchToken;
  }

  public double getPreviousScorePoints() {
    return previousScorePoints;
  }

  public void setPreviousScorePoints(double previousScorePoints) {
    this.previousScorePoints = previousScorePoints;
  }

  public double getNewScorePoints() {
    return newScorePoints;
  }

  public void setNewScorePoints(double newScorePoints) {
    this.newScorePoints = newScorePoints;
  }

  public double getScoreChange() {
    return scoreChange;
  }

  public void setScoreChange(double scoreChange) {
    this.scoreChange = scoreChange;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
