package com.gepardec.rest.model.dto;

import com.gepardec.model.MatchOutcome;
import com.gepardec.model.PlayerForm;

import java.util.List;

public record PlayerFormRestDto(String userToken,
                                String gameToken,
                                List<String> results,
                                long excludedMatches) {

    public PlayerFormRestDto(PlayerForm form) {
        this(form.userToken(), form.gameToken(),
                form.results().stream().map(MatchOutcome::name).toList(),
                form.excludedMatches());
    }
}
