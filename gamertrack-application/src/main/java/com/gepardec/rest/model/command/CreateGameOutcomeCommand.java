package com.gepardec.rest.model.command;

import com.gepardec.model.User;
import java.util.List;
import java.util.Set;

public record CreateGameOutcomeCommand(Long gameId, List<Long> userIds) {

}
