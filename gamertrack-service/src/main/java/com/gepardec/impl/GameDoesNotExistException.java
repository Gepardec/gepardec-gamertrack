package com.gepardec.impl;

public class GameDoesNotExistException extends RuntimeException {
  public GameDoesNotExistException(String message) {
    super(message);
  }

}
