package com.gepardec.model;

public class Game {

  private Long id;
  private String token;
  private String name;
  private String rules;

  public Game() {
  }

  public Game(Long id, String token, String name, String rules) {
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
  public String toString() {
    return "Game{" +
        "id=" + id +
        ", key='" + token + '\'' +
        ", name='" + name + '\'' +
        ", rules='" + rules + '\'' +
        '}';
  }
}
