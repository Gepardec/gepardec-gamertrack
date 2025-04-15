package com.gepardec.model;

import java.util.Objects;

public class Game {

  private Long id;
  private String token;
  private String name;
  private String rules;

  public Game() {
  }

  public Game(Long id, String token, String name, String rules) {
    this.id = id;
    this.name = name;
    this.rules = rules;
    this.token = token;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRules() {
    return rules;
  }

  public void setRules(String rules) {
    this.rules = rules;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Game game = (Game) o;
    return Objects.equals(id, game.id) && Objects.equals(token, game.token)
        && Objects.equals(name, game.name) && Objects.equals(rules, game.rules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, token, name, rules);
  }

  @Override
  public String toString() {
    return "Game{" +
        "id=" + id +
        ", key='" + token + '\'' +
        ", name='" + name + '\'' +
        ", rules='" + rules + '\'' +
        '}';
  }
}
