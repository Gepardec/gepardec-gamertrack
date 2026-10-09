package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record MatchRestDto(@NotBlank String token, @NotNull String createdOn, String updatedOn, @NotNull GameRestDto game,
                           @NotNull List<UserRestDto> users,
                           @Schema(description = "Placement per participating user token (1-based, tied users share a placement). Empty for matches created before outcomes were recorded.")
                           Map<String, Integer> outcome) {

    public MatchRestDto(Match match) {
        this(match.getToken(),
                match.getUpdatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                match.getUpdatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                new GameRestDto(match.getGame()),
                new ArrayList<>(
                        match.getUsers().stream()
                                .map(UserRestDto::new)
                                .toList()
                ),
                new HashMap<>(match.getOutcome() == null ? Map.of() : match.getOutcome()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchRestDto that = (MatchRestDto) o;
        return Objects.equals(game, that.game) && Objects.equals(token, that.token)
                && Objects.equals(users, that.users) && Objects.equals(createdOn, that.createdOn) && Objects.equals(updatedOn, that.updatedOn)
                && Objects.equals(outcome, that.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, game, users, createdOn, updatedOn, outcome);
    }

    @Override
    public String toString() {
        return "MatchRestDto{" +
                "token='" + token + '\'' +
                ", game=" + game +
                ", users=" + users +
                ", outcome=" + outcome +
                '}';
    }
}
