package com.gepardec.rest.model.command;

import java.util.List;

public record CreateMatchCommand(Long gameId, List<Long> userIds) {

}
