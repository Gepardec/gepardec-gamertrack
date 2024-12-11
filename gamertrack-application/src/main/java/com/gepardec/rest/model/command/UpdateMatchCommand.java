package com.gepardec.rest.model.command;

import java.util.List;

public record UpdateMatchCommand(Long gameId, List<Long> userIds) {

}
