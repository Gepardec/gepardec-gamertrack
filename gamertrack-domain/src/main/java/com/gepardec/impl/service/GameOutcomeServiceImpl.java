package com.gepardec.impl.service;

import com.gepardec.interfaces.repository.GameOutcomeRepository;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.GameOutcomeService;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.dtos.GameOutcomeDto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Transactional
public class GameOutcomeServiceImpl implements GameOutcomeService {

  private final Logger logger = LoggerFactory.getLogger(GameOutcomeServiceImpl.class);

  @Inject
  private GameOutcomeRepository gameOutcomeRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GameRepository gameRepository;


  @Override
  public Optional<GameOutcome> saveGameOutcome(GameOutcomeDto gameOutcomeDto) {
    logger.info(
        "Saving Gameoutcome containing GameID: %s and UserIDs: %s".formatted(
            gameOutcomeDto.gameId(), gameOutcomeDto.userIds()));

    if (!gameOutcomeDto.userIds().isEmpty()
        && userRepository.existsByUserId(gameOutcomeDto.userIds())
        && gameRepository.existsByGameId(gameOutcomeDto.gameId())) {
      return gameOutcomeRepository.saveGameOutcome(gameOutcomeDto);
    }

    return Optional.empty();

  }

  @Override
  public List<GameOutcome> findAllGameOutcomes() {
    logger.info("Getting all existing gameoutcomes");
    return gameOutcomeRepository.findAllGameOutcomes();
  }

  @Override
  public Optional<GameOutcome> findGameOutcomeById(Long id) {
    return gameOutcomeRepository.findGameOutcomeById(id);
  }

  @Override
  public Optional<GameOutcome> deleteGameOutcome(Long gameOutcomeId) {
    logger.info("Removing gameoutcome with ID: %s".formatted(gameOutcomeId));
    Optional<GameOutcome> gameOutcome = gameOutcomeRepository.findGameOutcomeById(gameOutcomeId);

    if (gameOutcome.isEmpty()) {
      logger.error(
          "Could not find gameOutcome with ID: %s when delete attempted".formatted(gameOutcomeId));
      return Optional.empty();
    }

    gameOutcomeRepository.deleteGameOutcome(gameOutcomeId);

    return gameOutcome;

  }

  @Override
  public Optional<GameOutcome> updateGameOutcome(GameOutcomeDto gameOutcomeDto) {

    logger.info("Updating gameoutcome with ID: %s");

    if (!gameOutcomeDto.userIds().isEmpty()
        && gameOutcomeDto.gameId() != null
        && userRepository.existsByUserId(gameOutcomeDto.userIds())
        && gameOutcomeRepository.existsGameOutcomeById(gameOutcomeDto.gameId())
        && gameRepository.existsByGameId(gameOutcomeDto.gameId())) {
      logger.info(
          "Saving updated gameoutcome with ID: %s having the following attributes: \n %s %s".formatted(
              gameOutcomeDto.id(), gameOutcomeDto.gameId(), gameOutcomeDto.userIds()));

      return gameOutcomeRepository.updateGameOutcome(gameOutcomeDto);
    }

    logger.info(
        "Saving updated gameoutcome with ID: %s aborted due to provided ID not existing".formatted(
            gameOutcomeDto.id()));
    return Optional.empty();
  }

  @Override
  public List<GameOutcome> findGameOutcomeByUserId(Long userId) {
    logger.info(
        "Getting all existing gameoutcomes that reference user with UserID: %s".formatted(userId));
    return gameOutcomeRepository.findGameOutcomeByUserId(userId);
  }

  @Override
  public List<GameOutcome> findGameOutcomesByGameId(Long gameId) {
    logger.info(
        "Getting all existing gameoutcomes that reference game with GameID: %s".formatted(gameId));
    return gameOutcomeRepository.findGameOutcomeByGameId(gameId);
  }
}
