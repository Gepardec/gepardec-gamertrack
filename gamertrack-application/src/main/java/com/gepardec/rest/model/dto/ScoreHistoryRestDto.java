package com.gepardec.rest.model.dto;

import com.gepardec.model.ScoreHistory;
import jakarta.validation.constraints.NotNull;

import java.time.format.DateTimeFormatter;

public record ScoreHistoryRestDto(@NotNull String token, @NotNull UserRestDto user,
                                  @NotNull GameRestDto game, @NotNull String matchToken,
                                  double previousScorePoints, double newScorePoints,
                                  double scoreChange, String createdOn) {

    public ScoreHistoryRestDto(ScoreHistory scoreHistory) {
        this(scoreHistory.getToken(), new UserRestDto(scoreHistory.getUser()),
                new GameRestDto(scoreHistory.getGame()), scoreHistory.getMatchToken(),
                scoreHistory.getPreviousScorePoints(), scoreHistory.getNewScorePoints(),
                scoreHistory.getScoreChange(),
                scoreHistory.getCreatedOn() == null
                        ? null
                        : scoreHistory.getCreatedOn()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
