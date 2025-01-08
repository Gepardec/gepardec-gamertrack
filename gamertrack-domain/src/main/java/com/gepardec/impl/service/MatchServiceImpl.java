package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.MatchService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
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
      logger.info(
          "Finding matches by userToken %s and gameToken %s".formatted(userToken, gameToken));
      return matchRepository.findMatchesByGameTokenAndUserToken(gameToken.get(), userToken.get());
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

    Optional<Game> foundGame = gameRepository.findGameByToken(match.getGame().getToken());
    List<User> foundUsers = match.getUsers().stream()
        .map(User::getToken)
        .map(token -> userRepository.findUserByToken(token))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();

    if (!foundUsers.isEmpty()
        && foundUsers.size() == match.getUsers().size()
        && foundGame.isPresent()) {

      match.setToken(tokenService.generateToken());
      match.setGame(foundGame.get());
      match.setUsers(foundUsers);

      logger.info(
          "Saving match containing GameID: %s and UserIDs: %s".formatted(
              match.getGame().getId(), match.getUsers().stream().map(User::getId).toList()));

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
  public Optional<Match> deleteMatch(String matchToken) {
    logger.info("Removing match with ID: %s".formatted(matchToken));
    Optional<Match> match = matchRepository.findMatchByToken(matchToken);

    if (match.isEmpty()) {
      logger.error(
          "Could not find match with Token: %s when delete attempted".formatted(matchToken));
      return Optional.empty();
    }

    matchRepository.deleteMatch(match.get().getId());

    return match;

  }

  @Override
  public Optional<Match> updateMatch(Match match) {

    logger.info("Updating match with ID: %s");

    Optional<Match> foundMatch = matchRepository.findMatchByToken(match.getToken());
    Optional<Game> foundGame = gameRepository.findGameByToken(match.getGame().getToken());
    List<User> foundUsers = match.getUsers().stream()
        .map(User::getToken)
        .map(token -> userRepository.findUserByToken(token))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();

    if (!foundUsers.isEmpty()
        && foundUsers.size() == match.getUsers().size()
        && foundGame.isPresent()
        && foundMatch.isPresent()) {

      match.setGame(foundGame.get());
      match.setUsers(foundUsers);
      match.setId(matchRepository.findMatchByToken(match.getToken()).get().getId());

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
