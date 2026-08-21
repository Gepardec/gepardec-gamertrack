package com.gepardec.model;

public record PlayerGameStats(String userToken,
                              String gameToken,
                              long matchesPlayed,
                              long wins,
                              long draws,
                              long losses,
                              double winRate,
                              Streak currentStreak,
                              int longestWinStreak,
                              long excludedMatches) {
}
