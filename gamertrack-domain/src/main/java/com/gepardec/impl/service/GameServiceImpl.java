package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.services.*;
import com.gepardec.foundation.exception.ApplicationException;
import com.gepardec.foundation.exception.ErrorMessage;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@Transactional
public class GameServiceImpl implements GameService, Serializable {

    private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Inject
    private GameRepository gameRepository;
    @Inject
    TokenService tokenService;
    @Inject
    private UserService userService;
    @Inject
    private ScoreService scoreService;

    @Inject
    private MatchService matchService;

    @Override
    public Optional<Game> saveGame(Game game) {
        logger.info("Saving game: %s".formatted(game));

        if (gameRepository.gameExistsByGameName(game.getName())) {
            String errorMessage = "Game with name %s already exists".formatted(game.getName());
            logger.error(errorMessage);
            throw new ApplicationException.GameAlreadyExistsException(new
                    ErrorMessage(errorMessage,
                    errorMessage + "\n Duplicate game not allowed. Choose another name or use the already existing game"));
        }
        game.setToken(tokenService.generateToken());
        Optional<Game> savedGame = gameRepository.saveGame(game);

        List<User> userList = userService.findAllUsers(true);
        if (!userList.isEmpty()) {

            for (User user : userList) {
                scoreService.saveScore(new Score(0L, user, savedGame.get(), 1500L, "", true));
            }
        }

        return savedGame;
    }

    @Override
    public Optional<Game> deleteGame(String token) {
        logger.info("Deleting game with token %s".formatted(token));
        Optional<Game> game = gameRepository.findGameByToken(token);
        if (game.isEmpty()) {
            return Optional.empty();
        }

        List<Match> matchesByGame = matchService.findAllMatches().stream().filter(score -> score.getGame().getToken().equals(token)).collect(Collectors.toList());

        List<Score> scoresByGame = scoreService.filterScores(null, null, null, token, true);

        for (Match match : matchesByGame) {
            matchService.deleteMatch(match.getToken());
        }

        for (Score score : scoresByGame) {
            scoreService.deleteScore(score.getToken());
        }

        gameRepository.deleteGame(game.get().getId());

        return game;
    }

    @Override
    public Optional<Game> updateGame(Game game) {
        Optional<Game> newGame = Optional.empty();

        if (game == null) {
            logger.error("Game is null");
            return Optional.empty();
        }

        logger.info("Updating game with token %s".formatted(game.getToken()));
        Optional<Game> gameOld = findGameByToken(game.getToken());

        if (gameOld.isPresent()) {
            game.setId(gameOld.get().getId());
            logger.info("Updating game with new values %s".formatted(game));
            newGame = gameRepository.updateGame(game);
            return newGame;
        }

        String errorMessage = "Game with token %s could not be found".formatted(game.getToken());

        logger.error(errorMessage);
        throw new ApplicationException.GameDoesNotExistException(
                new ErrorMessage(errorMessage, errorMessage +
                        "\n Game not found or might have been removed already. Please check the token and try again"));
    }

    @Override
    public Optional<Game> findGameByToken(String token) {
        logger.info("Finding game with token %s".formatted(token));
        return gameRepository.findGameByToken(token);
    }

    @Override
    public List<Game> findAllGames() {
        logger.info("Finding all games");
        return gameRepository.findAllGames();
    }
}

