package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.EloService;
import com.gepardec.core.services.MatchService;
import com.gepardec.core.services.ScoreService;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
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

    @Inject
    private EloService eloService;

    @Inject
    private ScoreService scoreService;


    @Override
    public List<Match> findAllFilteredOrUnfilteredMatches(
            Optional<String> gameToken,
            Optional<String> userToken,
            PageRequest pageRequest) {

        if (userToken.isPresent() && gameToken.isPresent()) {
            logger.info(
                    "Finding matches by userToken %s and gameToken %s".formatted(userToken, gameToken));
            return matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(gameToken.get(), userToken.get(), pageRequest);
        }

        return userToken
                .map(ut -> matchRepository
                        .findAllMatchesOrFilteredByGameTokenAndUserToken(null, ut, pageRequest))
                .orElseGet(() -> gameToken
                        .map(gt -> matchRepository
                                .findAllMatchesOrFilteredByGameTokenAndUserToken(gt, null, pageRequest))
                        .orElse(matchRepository.findAllMatchesOrFilteredByGameTokenAndUserToken(null, null, pageRequest)));
    }

    @Override
    public Optional<Match> saveMatch(Match match) {
        if (match.getUsers().size() >= 2) {
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

                Optional<Match> savedMatch = matchRepository.saveMatch(match);

                List<Score> scoreList = new ArrayList<>();

                for (User user : match.getUsers()) {
                    List<Score> filteredScores = scoreService.filterScores(null, null, user.getToken(), match.getGame().getToken(), true);
                    scoreList.add(filteredScores.isEmpty() ? null : filteredScores.getFirst());
                }

                List<Score> updatedScores = eloService.updateElo(match.getGame(), scoreList, match.getUsers());
                for (Score score : updatedScores) {
                    scoreService.updateScore(score);
                }
                return savedMatch;
            }

        }

        logger.error("Match.users.size()< 2 or foundUsers is empty");
        return Optional.empty();

    }

    @Override
    public List<Match> findAllMatches() {
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

    @Override
    public long countAllFilteredOrUnfilteredMatches(Optional<String> gameToken, Optional<String> userToken) {
        if (userToken.isPresent() && gameToken.isPresent()) {
            return matchRepository.countMatchesFilteredAndUnfiltered(gameToken.get(), userToken.get());
        }
        return userToken
                .map(ut -> matchRepository
                        .countMatchesFilteredAndUnfiltered(null, ut))
                .orElseGet(() -> gameToken
                        .map(gt -> matchRepository
                                .countMatchesFilteredAndUnfiltered(gt, null))
                        .orElse(matchRepository.countMatchesFilteredAndUnfiltered(null, null)));
    }
}
