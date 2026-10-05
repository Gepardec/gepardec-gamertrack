package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.core.repository.GameRepository;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.core.repository.TournamentRepository;
import com.gepardec.core.repository.UserRepository;
import com.gepardec.model.Game;
import com.gepardec.model.Match;
import com.gepardec.model.Tournament;
import com.gepardec.model.TournamentState;
import com.gepardec.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gepardec.TestFixtures.game;
import static com.gepardec.TestFixtures.match;
import static com.gepardec.TestFixtures.tournament;
import static com.gepardec.TestFixtures.user;

@io.quarkus.test.junit.QuarkusTest
public class TournamentRepositoryTest {

    @Inject
    EntityManager entityManager;

    @Inject
    private TournamentRepository tournamentRepository;

    @Inject
    private MatchRepository matchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private GameRepository gameRepository;

    @BeforeEach
    @Transactional
    public void before() throws Exception {
        entityManager.createQuery("UPDATE MatchEntity m SET m.tournament = null").executeUpdate();
        entityManager.createQuery("DELETE FROM TournamentEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM MatchEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM GameEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM UserEntity").executeUpdate();
    }

    private Tournament savedTournament() {
        Game savedGame = gameRepository.saveGame(game(null)).orElseThrow();
        User savedUser1 = userRepository.saveUser(user(null)).orElseThrow();
        User savedUser2 = userRepository.saveUser(user(null)).orElseThrow();

        Tournament tournament = tournament(null);
        tournament.setGame(savedGame);
        tournament.setParticipants(List.of(savedUser1, savedUser2));

        return tournamentRepository.saveTournament(tournament).orElseThrow();
    }

    @Test
    public void ensureSaveAndReadTournamentWorks() {
        Tournament savedTournament = savedTournament();

        Assertions.assertNotNull(savedTournament.getId());
        Assertions.assertEquals(TournamentState.CREATED, savedTournament.getState());
        Assertions.assertEquals(2, savedTournament.getParticipants().size());
        Assertions.assertNotNull(savedTournament.getCreatedOn());
    }

    @Test
    public void ensureFindAllTournamentsReturnsAllTournaments() {
        savedTournament();
        savedTournament();

        List<Tournament> foundTournaments = tournamentRepository.findAllTournaments();

        Assertions.assertEquals(2, foundTournaments.size());
    }

    @Test
    public void ensureFindTournamentByTokenForExistingTournamentReturnsTournament() {
        Tournament savedTournament = savedTournament();

        Optional<Tournament> foundTournament =
                tournamentRepository.findTournamentByToken(savedTournament.getToken());

        Assertions.assertTrue(foundTournament.isPresent());
        Assertions.assertEquals(savedTournament.getName(), foundTournament.get().getName());
    }

    @Test
    public void ensureFindTournamentByTokenForNonExistingTournamentReturnsEmptyOptional() {
        Assertions.assertTrue(tournamentRepository.findTournamentByToken("nonExisting").isEmpty());
    }

    @Test
    public void ensureUpdateTournamentUpdatesStateAndParticipants() {
        Tournament savedTournament = savedTournament();
        User additionalUser = userRepository.saveUser(user(null)).orElseThrow();

        savedTournament.setState(TournamentState.RUNNING);
        savedTournament.setParticipants(List.of(
                savedTournament.getParticipants().getFirst(),
                savedTournament.getParticipants().getLast(),
                additionalUser));

        Optional<Tournament> updatedTournament =
                tournamentRepository.updateTournament(savedTournament);

        Assertions.assertTrue(updatedTournament.isPresent());
        Assertions.assertEquals(TournamentState.RUNNING, updatedTournament.get().getState());
        Assertions.assertEquals(3, updatedTournament.get().getParticipants().size());
    }

    @Test
    public void ensureLinkMatchToTournamentAndFindMatchesByTournamentTokenWorks() {
        Tournament savedTournament = savedTournament();

        Match savedMatch = matchRepository.saveMatch(
                match(null, savedTournament.getGame(), savedTournament.getParticipants())).orElseThrow();

        tournamentRepository.linkMatchToTournament(savedTournament.getId(), savedMatch.getId());

        List<Match> foundMatches =
                tournamentRepository.findMatchesByTournamentToken(savedTournament.getToken());

        Assertions.assertEquals(1, foundMatches.size());
        Assertions.assertEquals(savedMatch.getToken(), foundMatches.getFirst().getToken());
    }

    @Test
    public void ensureDeleteTournamentKeepsMatchesUsersAndGame() {
        Tournament savedTournament = savedTournament();

        Match savedMatch = matchRepository.saveMatch(
                match(null, savedTournament.getGame(), savedTournament.getParticipants())).orElseThrow();
        tournamentRepository.linkMatchToTournament(savedTournament.getId(), savedMatch.getId());

        tournamentRepository.deleteTournament(savedTournament.getId());

        Assertions.assertTrue(
                tournamentRepository.findTournamentByToken(savedTournament.getToken()).isEmpty());
        Assertions.assertTrue(matchRepository.findMatchById(savedMatch.getId()).isPresent());
        Assertions.assertTrue(
                gameRepository.findGameByToken(savedTournament.getGame().getToken()).isPresent());
        Assertions.assertTrue(userRepository.findUserByToken(
                savedTournament.getParticipants().getFirst().getToken()).isPresent());
    }
}
