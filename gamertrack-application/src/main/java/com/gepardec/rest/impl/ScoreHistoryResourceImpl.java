package com.gepardec.rest.impl;

import com.gepardec.core.services.ScoreHistoryService;
import com.gepardec.rest.api.ScoreHistoryResource;
import com.gepardec.rest.model.dto.ScoreHistoryRestDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ScoreHistoryResourceImpl implements ScoreHistoryResource {

  @Inject
  private ScoreHistoryService scoreHistoryService;

  @Override
  public Response getScoreHistory(String userToken, String gameToken, String matchToken) {
    return Response.ok(
            scoreHistoryService.filterScoreHistory(userToken, gameToken, matchToken)
                .stream()
                .map(ScoreHistoryRestDto::new)
                .toList())
        .build();
  }

  @Override
  public Response getScoreHistoryByToken(String token) {
    return scoreHistoryService.findScoreHistoryByToken(token).map(ScoreHistoryRestDto::new)
        .map(Response::ok)
        .orElseGet(() -> Response.status(Response.Status.NO_CONTENT)).build();
  }

}
