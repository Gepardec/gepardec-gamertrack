package com.gepardec.rest.model.command;

import java.util.List;
import java.util.Set;

public record UpdateGameOutcomeCommand(Long gameOutcomeId, Long gameId, List<Long> userIds) {

}
