package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.repository.mapper.Mapper;
import com.gepardec.core.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class GameRepositoryImpl implements GameRepository, Serializable {

  @Inject
  private EntityManager em;

  @Inject
  private Mapper mapper;

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.Game> saveGame(Game gameDto) {
    com.gepardec.adapter.output.persistence.entity.Game game = mapper.toGame(gameDto);

    em.persist(game);
    em.flush();
    return Optional.ofNullable(em.find(com.gepardec.adapter.output.persistence.entity.Game.class, game.getId()));
  }

  @Override
  public void deleteGame(Long gameId) {
    em.remove(em.find(com.gepardec.adapter.output.persistence.entity.Game.class, gameId));
  }

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.Game> updateGame(Game gameDto) {
    Optional<com.gepardec.adapter.output.persistence.entity.Game> gameOld = findGameById(gameDto.id());
    return gameOld.map(game -> mapper.toGame(gameDto, game)).map(em::merge);
  }

  @Override
  public Optional<com.gepardec.adapter.output.persistence.entity.Game> findGameById(long id) {
    return Optional.ofNullable(em.find(com.gepardec.adapter.output.persistence.entity.Game.class, id));

  }

  @Override
  public List<com.gepardec.adapter.output.persistence.entity.Game> findAllGames() {
    return em.createQuery("select g from Game g", com.gepardec.adapter.output.persistence.entity.Game.class).getResultList();
  }

  @Override
  public Boolean gameExistsByGameName(String gameName) {
    Query query = em.createQuery("select g from Game g where g.name = :gameName", com.gepardec.adapter.output.persistence.entity.Game.class);
    query.setParameter("gameName", gameName);

    return !query.getResultList().isEmpty();
  }

  @Override
  public Boolean existsByGameId(Long gameId) {
    return findGameById(gameId).isPresent();
  }
}