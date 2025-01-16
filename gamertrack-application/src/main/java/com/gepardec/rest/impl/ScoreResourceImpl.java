package com.gepardec.rest.impl;

import com.gepardec.core.services.ScoreService;
import com.gepardec.rest.api.ScoreResource;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.dto.ScoreRestDto;
import com.gepardec.rest.model.mapper.RestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@RequestScoped
public class ScoreResourceImpl implements ScoreResource {

    @Inject
    private ScoreService scoreService;
    @Inject
    private RestMapper mapper;

    @Override
    public Response getScores(Double minPoints, Double maxPoints, Long userId, Long gameId,Boolean includeDeactivated) {
        return Response.ok(scoreService.filterScores(minPoints,maxPoints, userId,gameId,includeDeactivated)
                        .stream()
                        .map(ScoreRestDto::new)
                        .toList())
                .build();
    }

    @Override
    public Response getScoreById(Long id) {
        return scoreService.findScoreById(id).map(ScoreRestDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Response.Status.NO_CONTENT)).build();

    }

    @Override
    public Response getScoreByScorePoints(double points, Boolean includeDeactivated) {
        return Response.ok(scoreService.findScoreByScoresPoints(points, includeDeactivated)
                        .stream()
                        .map(ScoreRestDto::new)
                        .toList())
                .build();
    }

    //Only temporary for testing
    @Override
    public Response createScore(CreateScoreCommand score) {
        return scoreService.saveScore(mapper.createScoreCommandtoScore(score)).map(ScoreRestDto::new)
                .map(score1 -> Response.status(Response.Status.CREATED).entity(score1))
                .orElseGet(() ->  Response.status(Response.Status.NOT_FOUND)).build();
    }


}
