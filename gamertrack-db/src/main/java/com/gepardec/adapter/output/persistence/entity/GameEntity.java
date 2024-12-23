package com.gepardec.adapter.output.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "games")
public class GameEntity extends AbstractEntity {

  @NotBlank
  private String name;
  private String rules;


  public GameEntity(String name, String rules) {
    this.name = name;
    this.rules = rules;
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
}
