package com.gepardec.rest.impl;

import com.gepardec.interfaces.services.ScoreService;
import com.gepardec.rest.api.ScoreResource;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.dto.ScoreRestDto;
import com.gepardec.rest.model.mapper.RestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ScoreResourceImpl implements ScoreResource {

    @Inject
    private ScoreService scoreService;
    @Inject
    private RestMapper mapper;

    @Override
    public Response getScores(double minScore, double maxScore) {
        if (minScore == 0 && maxScore == 1.7976931348623157E308) {
            return scoreService.findAllScores().stream().map(ScoreRestDto::new).toList().isEmpty()
                    ? Response.status(Response.Status.NO_CONTENT).build()
                    : Response.ok().entity(scoreService.findAllScores().stream().map(ScoreRestDto::new).toList()).build();
        } else {
            return scoreService.findScoreByMinMaxScorePoints(minScore,maxScore).stream().map(ScoreRestDto::new).toList().isEmpty()
                    ? Response.status(Response.Status.NO_CONTENT).build()
                    : Response.ok().entity(scoreService.findScoreByMinMaxScorePoints(minScore,maxScore).stream().map(ScoreRestDto::new).toList()).build();
        }
    }

    @Override
    public Response getScoreById(Long id) {
        return scoreService.findScoreById(id).map(ScoreRestDto::new).map(Response::ok)
                .orElseGet(() ->  Response.status(Response.Status.NO_CONTENT)).build();

    }

    @Override
    public Response getScoreByUser(Long userId) {
        return scoreService.findScoreByUser(userId).stream().map(ScoreRestDto::new).toList().isEmpty()
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.ok().entity(scoreService.findScoreByUser(userId).stream().map(ScoreRestDto::new).toList()).build();

    }

    @Override
    public Response getScoreByGame(Long gameId) {
        return scoreService.findScoreByGame(gameId).stream().map(ScoreRestDto::new).toList().isEmpty()
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.ok().entity(scoreService.findScoreByGame(gameId).stream().map(ScoreRestDto::new).toList()).build();

    }

    @Override
    public Response getScoreByScorePoints(double points) {
        return scoreService.findScoreByScorePoints(points).stream().map(ScoreRestDto::new).toList().isEmpty()
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.ok().entity(scoreService.findScoreByScorePoints(points).stream().map(ScoreRestDto::new).toList()).build();
    }

    //Only temporary for testing
    @Override
    public Response createScore(CreateScoreCommand score) {
        return scoreService.saveScore(mapper.toScore(score)).map(ScoreRestDto::new)
                .map(score1 -> Response.status(Response.Status.CREATED).entity(score1))
                .orElseGet(() ->  Response.status(Response.Status.NOT_FOUND)).build();
    }


}
