package com.gepardec.rest.model.dto;

import com.gepardec.model.Score;

public record ScoreRestDto(Long id, UserRestDto userRestDto, GameRestDto gameRestDto, double score) {
    public ScoreRestDto(Score score){
        this(score.getId(), new UserRestDto(score.getUser()), new GameRestDto(score.getGame()), score.getScorePoints());
    }
}
