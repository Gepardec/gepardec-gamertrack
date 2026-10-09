package com.gepardec.rest.model.command;

import com.gepardec.model.Game;
import com.gepardec.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public record UpdateMatchCommand(Game game, List<User> users,
                                 @Schema(description = "Placement per participating user token (1-based). Tied users share a placement, the following placement skips the tied users (e.g. 1,1,3).",
                                     example = "{\"userToken1\": 1, \"userToken2\": 2}")
                                 Map<String, Integer> outcome) {

}
