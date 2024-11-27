package com.gepardec.service;

public class GameDoesNotExistException extends RuntimeException {
  public GameDoesNotExistException(String message) {
    super(message);
  }

}
