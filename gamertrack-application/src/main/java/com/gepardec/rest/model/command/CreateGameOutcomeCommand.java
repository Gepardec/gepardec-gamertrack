package com.gepardec.rest.model.command;

import java.util.List;

public record CreateGameOutcomeCommand(Long gameId, List<Long> userIds) {

}
