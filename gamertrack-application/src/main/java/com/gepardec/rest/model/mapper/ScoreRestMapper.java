package com.gepardec.rest.model.mapper;

import com.gepardec.model.Score;
import com.gepardec.rest.model.command.CreateScoreCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScoreRestMapper {

  public Score createScoreCommandtoScore(CreateScoreCommand scoreCommand) {
    return new Score(null, scoreCommand.user(), scoreCommand.game(), scoreCommand.scorePoints(),null);
  }


}
