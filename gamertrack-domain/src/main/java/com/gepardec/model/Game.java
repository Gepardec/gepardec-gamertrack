package com.gepardec.model;

import jakarta.validation.constraints.NotBlank;

public class Game {

  private Long id;
  @NotBlank(message = "Game name must not be null or blank")
  private String name;
  private String rules;

  public Game() {
  }

  public Game(Long id, String name, String rules) {
    this.id = id;
    this.name = name;
    this.rules = rules;
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
}
