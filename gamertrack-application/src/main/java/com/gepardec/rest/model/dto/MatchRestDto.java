package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record MatchRestDto(@NotBlank String token, @NotNull GameRestDto game,
                           @NotNull List<UserRestDto> users) {

    public MatchRestDto(Match match) {
        this(match.getToken(),
                new GameRestDto(match.getGame()),
                new ArrayList<>(
                        match.getUsers().stream()
                                .map(UserRestDto::new)
                                .toList()
                ));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchRestDto that = (MatchRestDto) o;
        return Objects.equals(game, that.game) && Objects.equals(token, that.token)
                && Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, game, users);
    }

    @Override
    public String toString() {
        return "MatchRestDto{" +
                "token='" + token + '\'' +
                ", game=" + game +
                ", users=" + users +
                '}';
    }
}
