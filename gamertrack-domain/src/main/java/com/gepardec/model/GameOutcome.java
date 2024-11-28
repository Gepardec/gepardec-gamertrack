package com.gepardec.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tables")
public class GameOutcome extends AbstractEntity {

  @ManyToOne()
  @JoinColumn(name = "fk_game", foreignKey = @ForeignKey(name = "fk_game"))
  private Game game;

  @ManyToMany
  @JoinTable(name = "gameoutcomes_users", joinColumns =
  @JoinColumn(name = "fk_user", foreignKey = @ForeignKey(name = "fk_user")), inverseJoinColumns =
  @JoinColumn(name = "fk_gameoutcome", foreignKey = @ForeignKey(name = "fk_gameoutcome")))
  private Set<User> users;

  public GameOutcome(Game game, Set<User> users) {
    this.game = game;
    this.users = users;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}
