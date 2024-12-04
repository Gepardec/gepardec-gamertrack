/*package com.gepardec.adapters.output.persistence.repository;

import com.gepardec.interfaces.repository.GameRepository;
import com.gepardec.model.Game;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
class GameRepositoryTest {

  @Inject
  private GameRepository repository;


  @Test
  void ensureWriteAndReadGameWorks() {
    Game game = new Game("TestGame", "Example Rules");

    repository.saveGame(game);
  }
}

 */