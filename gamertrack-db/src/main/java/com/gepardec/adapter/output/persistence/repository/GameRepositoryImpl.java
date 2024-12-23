package com.gepardec.adapter.output.persistence.repository;

import com.gepardec.adapter.output.persistence.entity.GameEntity;
import com.gepardec.adapter.output.persistence.repository.mapper.EntityMapper;
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
  private EntityMapper entityMapper;

  @Override
  public Optional<GameEntity> saveGame(Game gameDto) {
    GameEntity game = entityMapper.toGame(gameDto);

    em.persist(game);
    em.flush();
    return Optional.ofNullable(em.find(GameEntity.class, game.getId()));
  }

  @Override
  public void deleteGame(Long gameId) {
    em.remove(em.find(GameEntity.class, gameId));
  }

  @Override
  public Optional<GameEntity> updateGame(Game gameDto) {
    Optional<GameEntity> gameOld = findGameById(gameDto.id());
    return gameOld.map(game -> entityMapper.toGame(gameDto, game)).map(em::merge);
  }

  @Override
  public Optional<GameEntity> findGameById(long id) {
    return Optional.ofNullable(em.find(GameEntity.class, id));

  }

  @Override
  public List<GameEntity> findAllGames() {
    return em.createQuery("select g from GameEntity g", GameEntity.class).getResultList();
  }

  @Override
  public Boolean gameExistsByGameName(String gameName) {
    Query query = em.createQuery("select g from GameEntity g where g.name = :gameName", GameEntity.class);
    query.setParameter("gameName", gameName);

    return !query.getResultList().isEmpty();
  }

  @Override
  public Boolean existsByGameId(Long gameId) {
    return findGameById(gameId).isPresent();
  }
}