package com.gepardec.rest.model.dto;

import com.gepardec.model.Score;
import jakarta.validation.constraints.NotNull;

public record ScoreRestDto(@NotNull Long id, @NotNull UserRestDto user, @NotNull GameRestDto game, double score) {
    public ScoreRestDto(Score score){
        this(score.getId(), new UserRestDto(score.getUser()), new GameRestDto(score.getGame()), score.getScorePoints());
    }
}
