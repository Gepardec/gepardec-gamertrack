package com.gepardec.model;

public class Score{
    private Long id;
    private User user;
    private Game game;
    private double scorePoints;
    private String token;

    public Score(Long id) {
        this.id = id;
    }

    public Score(Long id, User user, Game game, double scorePoints, String token) {
        this.id = id;
        this.user = user;
        this.game = game;
        this.scorePoints = scorePoints;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
