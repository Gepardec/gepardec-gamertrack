package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gepardec.RestTestFixtures;
import com.gepardec.model.Score;
import com.gepardec.rest.model.command.CreateScoreCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScoreRestMapperTest {

  @InjectMocks
  private ScoreRestMapper scoreRestMapper;

  @Test
  public void ensureMapCreateScoreCommandToScoreDtoWorks() {
    CreateScoreCommand scoreCommand = RestTestFixtures.createScoreCommand(1L);

    Score mappedScore = scoreRestMapper.createScoreCommandtoScore(scoreCommand);

    assertEquals(scoreCommand.user().getId(), mappedScore.getUser().getId());
    assertEquals(scoreCommand.game().getId(), mappedScore.getGame().getId());
    assertEquals(scoreCommand.scorePoints(), mappedScore.getScorePoints());

  }

}
