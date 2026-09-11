package com.gepardec.rest.model.dto;

import com.gepardec.model.TournamentStanding;
import jakarta.validation.constraints.NotNull;

public record TournamentStandingRestDto(@NotNull UserRestDto user,
                                        long wins,
                                        long matchesPlayed) {

    public TournamentStandingRestDto(TournamentStanding standing) {
        this(new UserRestDto(standing.user()), standing.wins(), standing.matchesPlayed());
    }
}
