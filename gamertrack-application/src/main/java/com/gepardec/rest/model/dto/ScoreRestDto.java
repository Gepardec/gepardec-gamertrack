package com.gepardec.rest.model.dto;

import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;

public record ScoreRestDto(Long id, User user, Game game, double score) {
    public ScoreRestDto(Score score){
        this(score.getId(), score.getUser(), score.getGame(), score.getScorePoints());
    }

}
