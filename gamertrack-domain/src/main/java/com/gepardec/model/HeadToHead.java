package com.gepardec.model;

public record HeadToHead(String gameToken,
                         String firstUserToken,
                         String secondUserToken,
                         long matchesPlayed,
                         long firstUserWins,
                         long secondUserWins,
                         long draws,
                         long excludedMatches) {
}
