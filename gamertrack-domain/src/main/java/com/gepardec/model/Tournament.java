package com.gepardec.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Tournament {

    private Long id;
    private String token;
    @NotBlank(message = "Name must not be null or blank")
    private String name;
    @NotNull(message = "Game must not be null")
    private Game game;
    @NotNull(message = "Participants must not be null")
    private List<User> participants;
    private TournamentState state;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public Tournament() {
    }

    public Tournament(Long id, String token, String name, Game game, List<User> participants,
                      TournamentState state) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.game = game;
        this.participants = participants;
        this.state = state;
    }

    public Tournament(Long id, String token, String name, Game game, List<User> participants,
                      TournamentState state, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this(id, token, name, game, participants, state);
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public TournamentState getState() {
        return state;
    }

    public void setState(TournamentState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token)
                && Objects.equals(name, that.name) && Objects.equals(game, that.game)
                && Objects.equals(participants, that.participants) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, name, game, participants, state);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", game=" + game +
                ", participants=" + participants +
                ", state=" + state +
                '}';
    }
}
