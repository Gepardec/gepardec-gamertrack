package com.gepardec.model;

/**
 * Lifecycle of a tournament. The state only ever moves forward:
 * CREATED -> RUNNING -> DONE.
 */
public enum TournamentState {
    CREATED,
    RUNNING,
    DONE;

    public boolean canTransitionTo(TournamentState next) {
        return next != null && next.ordinal() > ordinal();
    }
}
