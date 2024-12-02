package com.gepardec.impl;

import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.GameOutcomeService;
import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Stateless
@Transactional
public class GameOutcomeServiceImpl implements GameOutcomeService {

  @Inject
  private GameOutcomeRepository gameOutcomeRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GameRepository gameRepository;


  @Override
  public Optional<GameOutcome> saveGameOutcome(Long gameId, List<Long> userIds) {
    List<User> users = userIds.stream()
        .map(userId -> userRepository.findUserReferencesById(userId))
        .flatMap(Optional::stream)
        .toList();

    Optional<Game> game = gameRepository.findGameReferenceByGameId(gameId);

    return users.size() == userIds.size() && game.isPresent()
        ? gameOutcomeRepository.saveGameOutcome(new GameOutcome(game.get(), users))
        : Optional.empty();
  }

  @Override
  public List<GameOutcome> findAllGameOutcomes() {
    var gameOutcomes =  gameOutcomeRepository.findAllGameOutcomes();

    for (GameOutcome gameOutcome : gameOutcomes) {
      System.out.println(gameOutcome);
    }

    return gameOutcomes;
  }

  @Override
  public Optional<GameOutcome> findGameOutcomeById(Long id) {
    return gameOutcomeRepository.findGameOutcomeById(id);
  }

  @Override
  public Optional<GameOutcome> deleteGameOutcome(Long gameOutcomeId) {
    Optional<GameOutcome> gameOutcome = gameOutcomeRepository.findGameOutcomeById(gameOutcomeId);

    if (gameOutcome.isEmpty()) {
      return Optional.empty();
    }

    gameOutcomeRepository.deleteGameOutcome(gameOutcomeId);

    return gameOutcome;

  }

  @Override
  public Optional<GameOutcome> updateGameOutcome(Long gameOutcomeId, Long gameId, List<Long> userIds) {

    List<User> users = userIds.stream().map(id -> userRepository.findUserReferencesById(id))
        .flatMap(Optional::stream)
        .toList();

    Optional<Game> game = gameRepository.findGameReferenceByGameId(gameId);

    Optional<GameOutcome> gameOutcomeOld = gameOutcomeRepository.findGameOutcomeById(gameOutcomeId);

    if ( users.size() == userIds.size() && game.isPresent() && gameOutcomeOld.isPresent()) {
      GameOutcome gameOutcomeNew = gameOutcomeOld.get();
      gameOutcomeNew.setGame(game.get());
      gameOutcomeNew.setUsers(users);
      System.out.println("usertosave");
      System.out.println(gameOutcomeNew);
      return gameOutcomeRepository.saveGameOutcome(gameOutcomeNew);
    }
    return Optional.empty();
  }

  @Override
  public List<GameOutcome> findGameOutcomeByUserId(Long userId) {
    System.out.println("Excecuting findbyuserId");
    return  gameOutcomeRepository.findGameOutcomeByUserId(userId);
  }

  @Override
  public List<GameOutcome> findGameOutcomesByGameId(Long gameId) {
    return gameOutcomeRepository.findGameOutcomeByGameId(gameId);
  }
}
