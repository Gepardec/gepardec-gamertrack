package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.MatchService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Collections;
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

  @Inject
  private TokenService tokenService;


  @Override
  public List<Match> findMatchesByGameTokenAndUserToken(Optional<String> gameToken,
      Optional<String> userToken) {

    if (userToken.isPresent() && gameToken.isPresent()) {
      logger.info("Finding matches by userId {} and gameId {}".formatted(userToken, gameToken));
      return matchRepository.findMatchesByGameTokenAndUserToken(userToken.get(), gameToken.get());
    }

    return userToken
        .map(ut -> matchRepository.findMatchesByUserToken(
            ut))
        .orElseGet(() -> gameToken
            .map(gt -> matchRepository.findMatchesByGameToken(
                gt))
            .orElse(Collections.emptyList()));
  }

  @Override
  public Optional<Match> saveMatch(Match match) {
    logger.info(
        "Saving match containing GameID: %s and UserIDs: %s".formatted(
            match.getGame().getId(), match.getUsers().stream().map(User::getId).toList()));

    if (!match.getUsers().isEmpty()
        && userRepository.existsByUserId(match.getUsers().stream().map(User::getId).toList())
        && gameRepository.existsByGameToken(match.getGame().getToken())) {
      match.setToken(tokenService.generateToken());
      return matchRepository.saveMatch(match);
    }

    return Optional.empty();

  }

  @Override
  public List<Match> findAllMatches() {
    logger.info("Getting all existing matches");
    return matchRepository.findAllMatches();
  }

  @Override
  public Optional<Match> findMatchByToken(String token) {
    return matchRepository.findMatchByToken(token);
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
  public Optional<Match> updateMatch(Match match) {

    logger.info("Updating match with ID: %s");

    if (!match.getUsers().isEmpty()
        && match.getGame().getId() != null
        && userRepository.existsByUserId(match.getUsers().stream().map(User::getId).toList())
        && matchRepository.existsMatchById(match.getGame().getId())
        && gameRepository.existsByGameToken(match.getGame().getToken())) {
      logger.info(
          "Saving updated match with ID: %s having the following attributes: \n %s %s".formatted(
              match.getId(), match.getGame().getId(),
              match.getUsers().stream().map(User::getId).toList()));

      return matchRepository.updateMatch(match);
    }

    logger.info(
        "Saving updated match with ID: %s aborted due to provided ID not existing".formatted(
            match.getId()));
    return Optional.empty();
  }
}
