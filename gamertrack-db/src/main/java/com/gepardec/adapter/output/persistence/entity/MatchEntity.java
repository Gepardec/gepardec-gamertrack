package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "matches", indexes = @Index(name = "ux_matches_token", columnList = "token", unique = true))
public class MatchEntity extends AbstractEntity {

  @Column(unique = true)
  private String token;

  @ManyToOne(optional = false)
  @JoinColumn(name = "fk_game_match", foreignKey = @ForeignKey(name = "fk_game_match"))
  private GameEntity game;

  // The list order is semantically meaningful (index 0 = winner, then 2nd, 3rd, ...):
  // the Elo calculation and the frontend placement display rely on it, so the
  // position of every user is persisted explicitly. When attaching a database that
  // predates the user_order column, it must be backfilled (NOT NULL) before startup.
  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(name = "matches_users", joinColumns =
  @JoinColumn(nullable = false, name = "fk_match", foreignKey = @ForeignKey(name = "fk_match")), inverseJoinColumns =
  @JoinColumn(nullable = false, name = "fk_user", foreignKey = @ForeignKey(name = "fk_user")))
  @OrderColumn(name = "user_order", nullable = false)
  private List<UserEntity> users;

  public MatchEntity() {

  }

  public MatchEntity(Long id, String token, GameEntity game, List<UserEntity> users) {
    this.id = id;
    this.token = token;
    this.game = game;
    this.users = users;
  }

  public GameEntity getGame() {
    return game;
  }

  public void setGame(GameEntity game) {
    this.game = game;
  }

  public List<UserEntity> getUsers() {
    return users;
  }

  public void setUsers(List<UserEntity> users) {
    this.users = users;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }


  @Override
  public String toString() {
    return "MatchEntity{" +
        "key='" + token + '\'' +
        ", game=" + game +
        ", users=" + users +
        '}';
  }


}
