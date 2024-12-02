package com.gepardec.model;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tables")
public class GameOutcome extends AbstractEntity {

  @ManyToOne()
  @JoinColumn(name = "fk_game", foreignKey = @ForeignKey(name = "fk_game"))
  private Game game;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinTable(name = "gameoutcomes_users", joinColumns =
  @JoinColumn(name = "fk_user", foreignKey = @ForeignKey(name = "fk_user")), inverseJoinColumns =
  @JoinColumn(name = "fk_gameoutcome", foreignKey = @ForeignKey(name = "fk_gameoutcome")))
  private List<User> users;

  public GameOutcome() {

  }
  public GameOutcome(Game game, List<User> users) {
    this.game = game;
    this.users = users;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }
}
