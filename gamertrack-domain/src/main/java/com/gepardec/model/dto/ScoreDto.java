package com.gepardec.model.dto;

import com.gepardec.model.Score;
import jakarta.validation.constraints.NotNull;

public record ScoreDto(@NotNull long id, @NotNull long userId, @NotNull long gameId, @NotNull double scorePoints) {
    public ScoreDto(Score score){
        this(score.getId(), score.user.getId(),score.game.getId(), score.scorePoints);
    }
}
