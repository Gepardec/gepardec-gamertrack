package com.gepardec.rest.model.dto;

import com.gepardec.model.PlayerGameStats;

public record PlayerGameStatsRestDto(String userToken,
                                     String gameToken,
                                     long matchesPlayed,
                                     long wins,
                                     long draws,
                                     long losses,
                                     double winRate,
                                     StreakRestDto currentStreak,
                                     int longestWinStreak,
                                     long excludedMatches) {

    public PlayerGameStatsRestDto(PlayerGameStats stats) {
        this(stats.userToken(), stats.gameToken(), stats.matchesPlayed(), stats.wins(),
                stats.draws(), stats.losses(), stats.winRate(),
                StreakRestDto.of(stats.currentStreak()), stats.longestWinStreak(),
                stats.excludedMatches());
    }
}
