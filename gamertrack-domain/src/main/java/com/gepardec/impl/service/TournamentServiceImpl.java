package com.gepardec.impl.service;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.TournamentRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.core.services.MatchService;
import com.gepardec.core.services.TokenService;
import com.gepardec.core.services.TournamentService;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Tournament;
import com.gepardec.model.TournamentStanding;
import com.gepardec.model.TournamentState;
import com.gepardec.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Manages tournaments on top of the existing match and rating world.
 * <p>
 * A tournament references a single game and at least two participants. Its state
 * only moves forward (CREATED -> RUNNING -> DONE); the participant list may only
 * change while the tournament is CREATED. Tournament matches are saved through the
 * regular {@link MatchService}, so rating changes behave exactly like normal
 * matches; the match result is derived from the order of the match's user list
 * (first place first, as already assumed by the Elo calculation).
 */
@ApplicationScoped
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private static final int MIN_PARTICIPANTS = 2;

    private final Logger logger = LoggerFactory.getLogger(TournamentServiceImpl.class);

    @Inject
    private TournamentRepository tournamentRepository;

    @Inject
    private GameRepository gameRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TokenService tokenService;

    @Inject
    private MatchService matchService;

    @Override
    public Optional<Tournament> saveTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().isBlank()) {
            logger.error("Tournament name must not be null or blank");
            return Optional.empty();
        }
        if (!hasEnoughDistinctParticipants(tournament.getParticipants())) {
            logger.error("Tournament needs at least %s distinct participants".formatted(MIN_PARTICIPANTS));
            return Optional.empty();
        }
        if (tournament.getGame() == null || tournament.getGame().getToken() == null) {
            logger.error("Tournament game must not be null");
            return Optional.empty();
        }

        Optional<Game> foundGame = gameRepository.findGameByToken(tournament.getGame().getToken());
        List<User> foundUsers = findExistingUsers(tournament.getParticipants());

        if (foundGame.isEmpty() || foundUsers.size() != tournament.getParticipants().size()) {
            logger.error("Tournament references a game or users that do not exist");
            return Optional.empty();
        }

        tournament.setToken(tokenService.generateToken());
        tournament.setGame(foundGame.get());
        tournament.setParticipants(foundUsers);
        tournament.setState(TournamentState.CREATED);

        logger.info("Saving tournament %s containing GameID: %s and UserIDs: %s".formatted(
                tournament.getName(), tournament.getGame().getId(),
                tournament.getParticipants().stream().map(User::getId).toList()));

        return tournamentRepository.saveTournament(tournament);
    }

    @Override
    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAllTournaments();
    }

    @Override
    public Optional<Tournament> findTournamentByToken(String token) {
        return tournamentRepository.findTournamentByToken(token);
    }

    @Override
    public Optional<Tournament> deleteTournament(String tournamentToken) {
        logger.info("Removing tournament with token: %s".formatted(tournamentToken));
        Optional<Tournament> tournament = tournamentRepository.findTournamentByToken(tournamentToken);

        if (tournament.isEmpty()) {
            logger.error("Could not find tournament with Token: %s when delete attempted"
                    .formatted(tournamentToken));
            return Optional.empty();
        }

        tournamentRepository.deleteTournament(tournament.get().getId());

        return tournament;
    }

    @Override
    public Optional<Tournament> updateTournamentState(String tournamentToken,
                                                      TournamentState newState) {
        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(tournamentToken);

        if (foundTournament.isEmpty()) {
            logger.error("Could not find tournament with Token: %s when state change attempted"
                    .formatted(tournamentToken));
            return Optional.empty();
        }

        Tournament tournament = foundTournament.get();
        if (!tournament.getState().canTransitionTo(newState)) {
            logger.error("Tournament state must only move forward, rejected %s -> %s"
                    .formatted(tournament.getState(), newState));
            return Optional.empty();
        }

        logger.info("Moving tournament %s from state %s to %s"
                .formatted(tournamentToken, tournament.getState(), newState));

        tournament.setState(newState);
        return tournamentRepository.updateTournament(tournament);
    }

    @Override
    public Optional<Tournament> addParticipant(String tournamentToken, String userToken) {
        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(tournamentToken);
        Optional<User> foundUser = userRepository.findUserByToken(userToken);

        if (foundTournament.isEmpty() || foundUser.isEmpty()) {
            logger.error("Could not find tournament %s or user %s when adding participant"
                    .formatted(tournamentToken, userToken));
            return Optional.empty();
        }

        Tournament tournament = foundTournament.get();
        if (tournament.getState() != TournamentState.CREATED) {
            logger.error("Participants can only be added while the tournament is CREATED");
            return Optional.empty();
        }
        if (containsUser(tournament.getParticipants(), userToken)) {
            logger.error("User %s is already a participant of tournament %s"
                    .formatted(userToken, tournamentToken));
            return Optional.empty();
        }

        List<User> participants = new ArrayList<>(tournament.getParticipants());
        participants.add(foundUser.get());
        tournament.setParticipants(participants);

        return tournamentRepository.updateTournament(tournament);
    }

    @Override
    public Optional<Tournament> removeParticipant(String tournamentToken, String userToken) {
        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(tournamentToken);

        if (foundTournament.isEmpty()) {
            logger.error("Could not find tournament %s when removing participant"
                    .formatted(tournamentToken));
            return Optional.empty();
        }

        Tournament tournament = foundTournament.get();
        if (tournament.getState() != TournamentState.CREATED) {
            logger.error("Participants can only be removed while the tournament is CREATED");
            return Optional.empty();
        }
        if (!containsUser(tournament.getParticipants(), userToken)) {
            logger.error("User %s is not a participant of tournament %s"
                    .formatted(userToken, tournamentToken));
            return Optional.empty();
        }
        if (tournament.getParticipants().size() - 1 < MIN_PARTICIPANTS) {
            logger.error("Tournament %s must keep at least %s participants"
                    .formatted(tournamentToken, MIN_PARTICIPANTS));
            return Optional.empty();
        }

        List<User> participants = tournament.getParticipants().stream()
                .filter(participant -> !participant.getToken().equals(userToken))
                .toList();
        tournament.setParticipants(participants);

        return tournamentRepository.updateTournament(tournament);
    }

    @Override
    public Optional<Match> saveTournamentMatch(String tournamentToken, List<User> users) {
        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(tournamentToken);

        if (foundTournament.isEmpty()) {
            logger.error("Could not find tournament %s when saving tournament match"
                    .formatted(tournamentToken));
            return Optional.empty();
        }

        Tournament tournament = foundTournament.get();
        if (tournament.getState() != TournamentState.RUNNING) {
            logger.error("Matches can only be played while the tournament is RUNNING");
            return Optional.empty();
        }
        if (!hasEnoughDistinctParticipants(users)) {
            logger.error("Tournament match needs at least %s distinct users".formatted(MIN_PARTICIPANTS));
            return Optional.empty();
        }
        if (!users.stream().allMatch(
                user -> containsUser(tournament.getParticipants(), user.getToken()))) {
            logger.error("Tournament matches may only contain registered participants");
            return Optional.empty();
        }

        Optional<Match> savedMatch =
                matchService.saveMatch(new Match(null, null, tournament.getGame(), users));

        savedMatch.ifPresent(match ->
                tournamentRepository.linkMatchToTournament(tournament.getId(), match.getId()));

        return savedMatch;
    }

    @Override
    public Optional<List<Match>> findTournamentMatches(String tournamentToken) {
        return tournamentRepository.findTournamentByToken(tournamentToken)
                .map(tournament -> tournamentRepository.findMatchesByTournamentToken(tournamentToken));
    }

    @Override
    public Optional<List<TournamentStanding>> findStandings(String tournamentToken) {
        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(tournamentToken);

        if (foundTournament.isEmpty()) {
            logger.error("Could not find tournament %s when computing standings"
                    .formatted(tournamentToken));
            return Optional.empty();
        }

        List<Match> matches = tournamentRepository.findMatchesByTournamentToken(tournamentToken);

        List<TournamentStanding> standings = foundTournament.get().getParticipants().stream()
                .map(participant -> standingOf(participant, matches))
                .sorted(Comparator.comparingLong(TournamentStanding::wins).reversed()
                        .thenComparing(Comparator.comparingLong(TournamentStanding::matchesPlayed).reversed())
                        .thenComparing(standing -> standing.user().getLastname(),
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(standing -> standing.user().getFirstname(),
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(standing -> standing.user().getToken()))
                .toList();

        return Optional.of(standings);
    }

    private TournamentStanding standingOf(User participant, List<Match> matches) {
        long matchesPlayed = matches.stream()
                .filter(match -> containsUser(match.getUsers(), participant.getToken()))
                .count();
        long wins = matches.stream()
                .filter(match -> match.getUsers().size() >= 2)
                .filter(match -> match.getUsers().getFirst().getToken().equals(participant.getToken()))
                .count();

        return new TournamentStanding(participant, wins, matchesPlayed);
    }

    private boolean hasEnoughDistinctParticipants(List<User> users) {
        return users != null
                && users.size() >= MIN_PARTICIPANTS
                && users.stream().map(User::getToken).distinct().count() == users.size();
    }

    private boolean containsUser(List<User> users, String userToken) {
        return users.stream().anyMatch(user -> user.getToken().equals(userToken));
    }

    private List<User> findExistingUsers(List<User> users) {
        return users.stream()
                .map(User::getToken)
                .map(token -> userRepository.findUserByToken(token))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
