package com.gepardec.rest.model.dto;

import com.gepardec.model.Tournament;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record TournamentRestDto(@NotBlank String token,
                                @NotBlank String name,
                                @NotNull String state,
                                String createdOn,
                                String updatedOn,
                                @NotNull GameRestDto game,
                                @NotNull List<UserRestDto> participants) {

    public TournamentRestDto(Tournament tournament) {
        this(tournament.getToken(),
                tournament.getName(),
                tournament.getState() != null ? tournament.getState().name() : null,
                format(tournament.getCreatedOn()),
                format(tournament.getUpdatedOn()),
                new GameRestDto(tournament.getGame()),
                tournament.getParticipants().stream()
                        .map(UserRestDto::new)
                        .toList());
    }

    private static String format(LocalDateTime dateTime) {
        return dateTime == null ? null
                : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
