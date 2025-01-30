package com.gepardec.rest.impl;

import com.gepardec.core.services.ScoreService;
import com.gepardec.rest.api.ScoreResource;
import com.gepardec.rest.model.dto.ScoreRestDto;
import com.gepardec.rest.model.mapper.ScoreRestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ScoreResourceImpl implements ScoreResource {

  @Inject
  private ScoreService scoreService;
  @Inject
  private ScoreRestMapper mapper;

  @Override
  public Response getScores(Double minPoints, Double maxPoints, String userToken, String gameToken,
      Boolean includeDeactivated) {
    return Response.ok(
            scoreService.filterScores(minPoints, maxPoints, userToken, gameToken, includeDeactivated)
                .stream()
                .map(ScoreRestDto::new)
                .toList())
        .build();
  }

  @Override
  public Response getScoreByToken(String token) {
    return scoreService.findScoreByToken(token).map(ScoreRestDto::new).map(Response::ok)
        .orElseGet(() -> Response.status(Response.Status.NO_CONTENT)).build();
  }

  @Override
  public Response getScoreByScorePoints(double points, Boolean includeDeactivated) {
    return Response.ok(scoreService.findScoreByScoresPoints(points, includeDeactivated)
            .stream()
            .map(ScoreRestDto::new)
            .toList())
        .build();
  }

}
