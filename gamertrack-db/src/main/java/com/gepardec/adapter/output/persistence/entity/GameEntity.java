package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "games", indexes = @Index(name = "ux_games_token", columnList = "token", unique = true))
public class GameEntity extends AbstractEntity {

  @Column(name = "token", unique = true)
  private String token;
  @NotBlank
  private String name;
  private String rules;


  public GameEntity(Long id, String token, String name, String rules) {
    this.id = id;
    this.name = name;
    this.rules = rules;
    this.token = token;
  }

  public GameEntity() {

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
    return "GameEntity{" +
        "id=" + id + '\'' +
        "key='" + token + '\'' +
        ", name='" + name + '\'' +
        ", rules='" + rules + '\'' +
        '}';
  }

}
