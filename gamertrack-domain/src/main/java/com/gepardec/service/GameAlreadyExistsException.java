package com.gepardec.service;

public class GameAlreadyExistsException extends RuntimeException {

  public GameAlreadyExistsException(String message) {
    super(message);
  }
}
