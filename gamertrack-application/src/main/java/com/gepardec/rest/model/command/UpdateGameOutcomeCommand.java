package com.gepardec.rest.model.command;

import java.util.List;

public record UpdateGameOutcomeCommand(Long gameOutcomeId, Long gameId, List<Long> userIds) {

}
