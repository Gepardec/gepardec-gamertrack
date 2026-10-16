package com.gepardec.rest.model.dto;

import com.gepardec.model.Streak;

public record StreakRestDto(String type, int length) {

    public static StreakRestDto of(Streak streak) {
        return streak == null
                ? new StreakRestDto("NONE", 0)
                : new StreakRestDto(streak.type().name(), streak.length());
    }
}
