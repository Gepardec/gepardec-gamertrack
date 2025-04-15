package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record MatchRestDto(@NotBlank String token, @NotNull String createdOn, String updatedOn, @NotNull GameRestDto game,
                           @NotNull List<UserRestDto> users) {

    public MatchRestDto(Match match) {
        this(match.getToken(),
                match.getUpdatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                match.getUpdatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
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
                && Objects.equals(users, that.users) && Objects.equals(createdOn, that.createdOn) && Objects.equals(updatedOn, that.updatedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, game, users,createdOn, updatedOn);
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
