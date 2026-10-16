package com.gepardec.rest.model.dto;

import com.gepardec.model.HeadToHead;

public record HeadToHeadRestDto(String gameToken,
                                String firstUserToken,
                                String secondUserToken,
                                long matchesPlayed,
                                long firstUserWins,
                                long secondUserWins,
                                long draws,
                                long excludedMatches) {

    public HeadToHeadRestDto(HeadToHead headToHead) {
        this(headToHead.gameToken(), headToHead.firstUserToken(), headToHead.secondUserToken(),
                headToHead.matchesPlayed(), headToHead.firstUserWins(), headToHead.secondUserWins(),
                headToHead.draws(), headToHead.excludedMatches());
    }
}
