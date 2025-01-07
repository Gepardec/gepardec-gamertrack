package com.gepardec.rest.model.dto;

import com.gepardec.model.Match;
import java.util.ArrayList;
import java.util.List;

public record MatchRestDto(Long id, GameRestDto gameRestDto, List<UserRestDto> userRestDtos) {

  public MatchRestDto(Match match) {
    this(match.getId(),
        new GameRestDto(match.getGame()),
        new ArrayList<>(
            match.getUsers().stream()
                .map(UserRestDto::new)
                .toList()
        ));
  }
}
