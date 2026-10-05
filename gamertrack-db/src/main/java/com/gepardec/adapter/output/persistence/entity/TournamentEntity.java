package com.gepardec.adapter.output.persistence.entity;

import com.gepardec.model.TournamentState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "tournaments", indexes = @Index(name = "ux_tournaments_token", columnList = "token", unique = true))
public class TournamentEntity extends AbstractEntity {

  @Column(unique = true)
  private String token;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TournamentState state;

  @ManyToOne(optional = false)
  @JoinColumn(name = "fk_game_tournament", foreignKey = @ForeignKey(name = "fk_game_tournament"))
  private GameEntity game;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(name = "tournaments_users", joinColumns =
  @JoinColumn(nullable = false, name = "fk_tournament", foreignKey = @ForeignKey(name = "fk_tournament_participant")), inverseJoinColumns =
  @JoinColumn(nullable = false, name = "fk_user", foreignKey = @ForeignKey(name = "fk_user_participant")))
  private List<UserEntity> participants;

  public TournamentEntity() {

  }

  public TournamentEntity(Long id, String token, String name, TournamentState state,
      GameEntity game, List<UserEntity> participants) {
    this.id = id;
    this.token = token;
    this.name = name;
    this.state = state;
    this.game = game;
    this.participants = participants;
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

  public TournamentState getState() {
    return state;
  }

  public void setState(TournamentState state) {
    this.state = state;
  }

  public GameEntity getGame() {
    return game;
  }

  public void setGame(GameEntity game) {
    this.game = game;
  }

  public List<UserEntity> getParticipants() {
    return participants;
  }

  public void setParticipants(List<UserEntity> participants) {
    this.participants = participants;
  }

  @Override
  public String toString() {
    return "TournamentEntity{" +
        "key='" + token + '\'' +
        ", name='" + name + '\'' +
        ", state=" + state +
        ", game=" + game +
        ", participants=" + participants +
        '}';
  }
}
