package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.adapters.output.persistence.repository.mapper.Mapper;
import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.model.Game;
import com.gepardec.model.dtos.GameDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GameRepositoryImpl implements GameRepository, Serializable {


  @Inject
  private EntityManager em;

  @Inject
  private Mapper mapper;

  @Override
  public Optional<Game> saveGame(GameDto gameDto) {

    Game game = mapper.toGame(gameDto);

    em.persist(game);
    return Optional.ofNullable(em.find(Game.class, game.getId()));
  }

  @Override
  public void deleteGame(Long gameId) {
    em.remove(em.find(Game.class, gameId));
  }

  @Override
  public Optional<Game> updateGame(GameDto gameDto) {
    Optional<Game> gameOld = findGameById(gameDto.id());
    return gameOld.map(game -> mapper.toGame(gameDto, game)).map(em::merge);
  }

  @Override
  public Optional<Game> findGameById(long id) {
    return Optional.ofNullable(em.find(Game.class, id));

  }

  @Override
  public List<Game> findAllGames() {
    return em.createQuery("select g from Game g", Game.class).getResultList();
  }

  @Override
  public Boolean GameExistsByGameName(String gameName) {
    Query query = em.createQuery("select g from Game g where g.name = :gameName", Game.class);
    query.setParameter("gameName", gameName);

    return !query.getResultList().isEmpty();
  }

  @Override
  public Optional<Game> findGameReferenceByGameId(Long gameId) {
    return Optional.of(em.getReference(Game.class, gameId));
  }

  @Override
  public Boolean existsByGameId(Long gameId) {
    return findGameById(gameId).isPresent();
  }
}