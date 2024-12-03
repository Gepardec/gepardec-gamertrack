package com.gepardec.rest.model.dto;

import com.gepardec.model.Score;
import jakarta.validation.constraints.NotNull;

public record ScoreDto (@NotNull long userId, @NotNull long gameId, @NotNull double score) {
    public ScoreDto(Score score){
        this(score.user.getId(),score.game.getId(), score.scorePoints);
    }

}
