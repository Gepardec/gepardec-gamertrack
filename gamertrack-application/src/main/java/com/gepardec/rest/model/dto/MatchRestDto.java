package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public record MatchRestDto(@NotBlank String token, @NotNull GameRestDto gameRestDto,
                           @NotNull List<UserRestDto> userRestDtos) {

  public MatchRestDto(Match match) {
    this(match.getToken(),
        new GameRestDto(match.getGame()),
        new ArrayList<>(
            match.getUsers().stream()
                .map(UserRestDto::new)
                .toList()
        ));
  }
}
