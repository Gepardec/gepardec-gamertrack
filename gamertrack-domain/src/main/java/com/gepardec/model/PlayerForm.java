package com.gepardec.model;

import java.util.List;

public record PlayerForm(String userToken,
                         String gameToken,
                         List<MatchOutcome> results,
                         long excludedMatches) {
}
