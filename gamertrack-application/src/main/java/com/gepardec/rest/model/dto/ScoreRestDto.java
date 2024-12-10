package com.gepardec.rest.model.dto;

import com.gepardec.model.Score;
import jakarta.validation.constraints.NotNull;

public record ScoreRestDto(@NotNull long userId, @NotNull long gameId, @NotNull double score) {
    public ScoreRestDto(Score score){
        this(score.user.getId(),score.game.getId(), score.scorePoints);
    }

}
