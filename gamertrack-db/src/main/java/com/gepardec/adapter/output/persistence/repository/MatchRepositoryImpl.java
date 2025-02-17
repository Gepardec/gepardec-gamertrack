package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.MatchEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.MatchMapper;
import com.gepardec.core.repository.MatchRepository;
import com.gepardec.model.Match;
import jakarta.data.page.PageRequest;
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
public class MatchRepositoryImpl implements MatchRepository {

    private final Logger logger = LoggerFactory.getLogger(MatchRepositoryImpl.class);

    @Inject
    private EntityManager em;

    @Inject
    MatchMapper matchMapper;

    @Override
    public Optional<Match> saveMatch(Match match) {
        MatchEntity matchToSave = matchMapper.matchModelToMatchEntityWithReference(match);
        logger.info("Saving  match {}", match);

        em.persist(matchToSave);
        em.flush();

        return findMatchById(matchToSave.getId());
    }

    @Override
    public List<Match> findAllMatches() {
        logger.info("Finding all matches");
        return findAllMatches(PageRequest.ofPage(1L, Integer.MAX_VALUE, true));
    }

    @Override
    public List<Match> findAllMatches(PageRequest pageRequest) {
        logger.info("Finding all matches");
        return em.createQuery("select go from MatchEntity go", MatchEntity.class)
                .setFirstResult(Math.max(0, ((int) pageRequest.page() - 1) * pageRequest.size()))
                .setMaxResults(pageRequest.size())
                .getResultList()
                .stream()
                .map(matchMapper::matchEntityToMatchModel)
                .toList();
    }

    @Override
    public Optional<Match> findMatchById(Long id) {
        logger.info("Finding match by id: %s".formatted(id));

        return Optional.ofNullable(em.find(MatchEntity.class, id))
                .map(matchMapper::matchEntityToMatchModel);
    }

    @Override
    public Optional<Match> findMatchByToken(String token) {
        logger.info("Finding match by token: %s".formatted(token));

        var found = em.createQuery(
                        "select m from MatchEntity m where m.token = :token",
                        MatchEntity.class)
                .setParameter("token", token)
                .getResultList().stream().findFirst();

        return found.map(matchEntity -> matchMapper.matchEntityToMatchModel(matchEntity));
    }

    @Override

    public void deleteMatch(Long matchId) {
        Optional<MatchEntity> matchToDelete = Optional.ofNullable(em.find(MatchEntity.class, matchId));

        matchToDelete.ifPresentOrElse(
                mtd -> logger.info("Deleting match with id: %s".formatted(mtd.getId())),
                () -> logger.info("Could not find match with ID %s".formatted(matchId)));

        em.remove(matchToDelete.get());
    }

    @Override
    public Optional<Match> updateMatch(Match matchNew) {
        logger.info("updating match with id: %s".formatted(matchNew.getId()));

        MatchEntity match = em.find(MatchEntity.class, matchNew.getId());
        if (match == null) {
            return Optional.empty();
        }

        MatchEntity updatedMatch = em.merge(
                matchMapper.matchModelToMatchEntityWithReference(matchNew, match));

        return Optional.of(matchMapper.matchEntityToMatchModel(updatedMatch));
    }

    @Override
    public List<Match> findMatchesByUserToken(String userToken) {
        logger.info("Finding all matches by user token: %s".formatted(userToken));
        var query = em.createQuery(
                "select m from MatchEntity m inner join m.users u where u.token = :userToken ",
                MatchEntity.class);

        query.setParameter("userToken", userToken);
        return query.getResultList().stream().map(matchMapper::matchEntityToMatchModel).toList();
    }

    @Override
    public List<Match> findMatchesByGameToken(String gameToken, PageRequest pageRequest) {
        logger.info("Finding all games outcomes by game token: %s".formatted(gameToken));

        int pageIndex = (int) pageRequest.page() - 1;
        int offset = Math.max(0, pageIndex * pageRequest.size());

        var query = em.createQuery("select m from MatchEntity m where m.game.token = :gameToken order by  m.id desc",
                MatchEntity.class);

        query.setParameter("gameToken", gameToken);
        query.setFirstResult(offset);
        query.setMaxResults(pageRequest.size());
        return query.getResultList().stream().map(matchMapper::matchEntityToMatchModel).toList();
    }

    @Override
    public List<Match> findMatchesByGameTokenAndUserToken(String gameToken, String userToken, PageRequest pageRequest) {
        var query = em.createQuery(
                "select m from MatchEntity m inner join m.users u where (:userToken is NULL OR u.token = :userToken) and (:gameToken is NULL OR m.game.token = :gameToken) order by m.id desc",
                MatchEntity.class);


        int pageIndex = (int) pageRequest.page() - 1;
        int offset = Math.max(0, pageIndex * pageRequest.size());

        query.setParameter("userToken", userToken);
        query.setParameter("gameToken", gameToken);

        query.setFirstResult(offset);
        query.setMaxResults(pageRequest.size());


        return query.getResultList().stream().map(matchMapper::matchEntityToMatchModel).toList();

    }

    @Override
    public Boolean existsMatchById(Long matchId) {
        return findMatchById(matchId).isPresent();
    }
}
