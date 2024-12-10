package com.gepardec.impl.service;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.interfaces.repository.MatchRepository;
import com.gepardec.interfaces.repository.UserRepository;
import com.gepardec.interfaces.services.MatchService;
import com.gepardec.model.Match;
import com.gepardec.model.dtos.MatchDto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Transactional
public class MatchServiceImpl implements MatchService {

  private final Logger logger = LoggerFactory.getLogger(MatchServiceImpl.class);

  @Inject
  private MatchRepository matchRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private GameRepository gameRepository;


  @Override
  public Optional<Match> saveMatch(MatchDto matchDto) {
    logger.info(
        "Saving match containing GameID: %s and UserIDs: %s".formatted(
            matchDto.gameId(), matchDto.userIds()));

    if (!matchDto.userIds().isEmpty()
        && userRepository.existsByUserId(matchDto.userIds())
        && gameRepository.existsByGameId(matchDto.gameId())) {
      return matchRepository.saveMatch(matchDto);
    }

    return Optional.empty();

  }

  @Override
  public List<Match> findAllMatches() {
    logger.info("Getting all existing matches");
    return matchRepository.findAllMatches();
  }

  @Override
  public Optional<Match> findMatchById(Long id) {
    return matchRepository.findMatchById(id);
  }

  @Override
  public Optional<Match> deleteMatch(Long matchId) {
    logger.info("Removing match with ID: %s".formatted(matchId));
    Optional<Match> match = matchRepository.findMatchById(matchId);

    if (match.isEmpty()) {
      logger.error(
          "Could not find match with ID: %s when delete attempted".formatted(matchId));
      return Optional.empty();
    }

    matchRepository.deleteMatch(matchId);

    return match;

  }

  @Override
  public Optional<Match> updateMatch(MatchDto matchDto) {

    logger.info("Updating match with ID: %s");

    if (!matchDto.userIds().isEmpty()
        && matchDto.gameId() != null
        && userRepository.existsByUserId(matchDto.userIds())
        && matchRepository.existsMatchById(matchDto.gameId())
        && gameRepository.existsByGameId(matchDto.gameId())) {
      logger.info(
          "Saving updated match with ID: %s having the following attributes: \n %s %s".formatted(
              matchDto.id(), matchDto.gameId(), matchDto.userIds()));

      return matchRepository.updateMatch(matchDto);
    }

    logger.info(
        "Saving updated match with ID: %s aborted due to provided ID not existing".formatted(
            matchDto.id()));
    return Optional.empty();
  }

  @Override
  public List<Match> findMatchByUserId(Long userId) {
    logger.info(
        "Getting all existing matches that reference user with UserID: %s".formatted(userId));
    return matchRepository.findMatchByUserId(userId);
  }

  @Override
  public List<Match> findMatchsByGameId(Long gameId) {
    logger.info(
        "Getting all existing matches that reference game with GameID: %s".formatted(gameId));
    return matchRepository.findMatchByGameId(gameId);
  }
}
