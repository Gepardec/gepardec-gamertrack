package com.gepardec.rest.model.dto;

import com.gepardec.model.Score;
import jakarta.validation.constraints.NotNull;

public record ScoreRestDto(@NotNull String token, @NotNull UserRestDto user, @NotNull GameRestDto game, double score) {
    public ScoreRestDto(Score score){
        this(score.getToken(), new UserRestDto(score.getUser()), new GameRestDto(score.getGame()), score.getScorePoints());
    }

}
