package com.gepardec.model;

import java.time.LocalDateTime;

public class ScoreHistory {
    private Long id;
    private String token;
    private User user;
    private Game game;
    private String matchToken;
    private double previousScorePoints;
    private double newScorePoints;
    private double scoreChange;
    private LocalDateTime createdOn;

    public ScoreHistory() {
    }

    public ScoreHistory(Long id, String token, User user, Game game, String matchToken,
                        double previousScorePoints, double newScorePoints, double scoreChange) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.game = game;
        this.matchToken = matchToken;
        this.previousScorePoints = previousScorePoints;
        this.newScorePoints = newScorePoints;
        this.scoreChange = scoreChange;
    }

    public ScoreHistory(Long id, String token, User user, Game game, String matchToken,
                        double previousScorePoints, double newScorePoints, double scoreChange,
                        LocalDateTime createdOn) {
        this(id, token, user, game, matchToken, previousScorePoints, newScorePoints, scoreChange);
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
