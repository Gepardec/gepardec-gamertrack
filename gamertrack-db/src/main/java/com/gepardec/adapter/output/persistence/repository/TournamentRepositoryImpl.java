package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.entity.TournamentEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.MatchMapper;
import com.gepardec.adapter.output.persistence.repository.mapper.TournamentMapper;
import com.gepardec.core.repository.TournamentRepository;
import com.gepardec.model.Match;
import com.gepardec.model.Tournament;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class TournamentRepositoryImpl implements TournamentRepository {

    private final Logger logger = LoggerFactory.getLogger(TournamentRepositoryImpl.class);

    @Inject
    private EntityManager em;

    @Inject
    TournamentMapper tournamentMapper;

    @Inject
    MatchMapper matchMapper;

    @Override
    public Optional<Tournament> saveTournament(Tournament tournament) {
        TournamentEntity tournamentToSave =
                tournamentMapper.tournamentModelToTournamentEntityWithReference(tournament);
        logger.info("Saving tournament {}", tournament);

        em.persist(tournamentToSave);
        em.flush();

        return findTournamentById(tournamentToSave.getId());
    }

    @Override
    public List<Tournament> findAllTournaments() {
        logger.info("Finding all tournaments");
        return em.createQuery("select t from TournamentEntity t order by t.id desc",
                        TournamentEntity.class)
                .getResultList()
                .stream()
                .map(tournamentMapper::tournamentEntityToTournamentModel)
                .toList();
    }

    @Override
    public Optional<Tournament> findTournamentById(Long id) {
        logger.info("Finding tournament by id: %s".formatted(id));

        return Optional.ofNullable(em.find(TournamentEntity.class, id))
                .map(tournamentMapper::tournamentEntityToTournamentModel);
    }

    @Override
    public Optional<Tournament> findTournamentByToken(String token) {
        logger.info("Finding tournament by token: %s".formatted(token));

        return em.createQuery(
                        "select t from TournamentEntity t where t.token = :token",
                        TournamentEntity.class)
                .setParameter("token", token)
                .getResultList().stream().findFirst()
                .map(tournamentMapper::tournamentEntityToTournamentModel);
    }

    @Override
    public Optional<Tournament> updateTournament(Tournament tournamentNew) {
        logger.info("updating tournament with id: %s".formatted(tournamentNew.getId()));

        TournamentEntity tournament = em.find(TournamentEntity.class, tournamentNew.getId());
        if (tournament == null) {
            return Optional.empty();
        }

        TournamentEntity updatedTournament = em.merge(
                tournamentMapper.tournamentModelToTournamentEntityWithReference(tournamentNew,
                        tournament));

        return Optional.of(tournamentMapper.tournamentEntityToTournamentModel(updatedTournament));
    }

    @Override
    public void deleteTournament(Long tournamentId) {
        Optional<TournamentEntity> tournamentToDelete =
                Optional.ofNullable(em.find(TournamentEntity.class, tournamentId));

        tournamentToDelete.ifPresentOrElse(
                ttd -> logger.info("Deleting tournament with id: %s".formatted(ttd.getId())),
                () -> logger.info("Could not find tournament with ID %s".formatted(tournamentId)));

        //Unlink the played matches first, they (and their rating changes) must survive the tournament
        em.createQuery("update MatchEntity m set m.tournament = null where m.tournament.id = :tournamentId")
                .setParameter("tournamentId", tournamentId)
                .executeUpdate();

        em.remove(tournamentToDelete.get());
    }

    @Override
    public List<Match> findMatchesByTournamentToken(String tournamentToken) {
        logger.info("Finding matches of tournament with token: %s".formatted(tournamentToken));

        return em.createQuery(
                        "select m from MatchEntity m where m.tournament.token = :tournamentToken order by m.id",
                        MatchEntity.class)
                .setParameter("tournamentToken", tournamentToken)
                .getResultList()
                .stream()
                .map(matchMapper::matchEntityToMatchModel)
                .toList();
    }

    @Override
    public void linkMatchToTournament(Long tournamentId, Long matchId) {
        logger.info("Linking match with id %s to tournament with id %s"
                .formatted(matchId, tournamentId));

        MatchEntity match = em.find(MatchEntity.class, matchId);
        match.setTournament(em.getReference(TournamentEntity.class, tournamentId));
    }
}
