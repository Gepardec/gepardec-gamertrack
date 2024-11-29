package com.gepardec.rest.impl;

import com.gepardec.interfaces.services.ScoreService;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.rest.api.ScoreResource;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.dto.ScoreDto;
import com.gepardec.rest.model.dto.UserDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class ScoreResourceImpl implements ScoreResource {

    @Inject
    private ScoreService scoreService;

    @Override
    public Response getScores() {
        return Response.ok().entity(scoreService.findAllScores().stream().map(ScoreDto::new).toList()).build();
    }

    @Override
    public Response getScoreById(Long id) {
        return Response.ok().entity(scoreService.findById(id).stream().map(ScoreDto::new).toList()).build();

    }

    @Override
    public Response getScoreByUser(Long userId) {
        return Response.ok().entity(scoreService.findByUser(userId).stream().map(ScoreDto::new).toList()).build();

    }

    @Override
    public Response getScoreByGame(Long gameId) {
        return Response.ok().entity(scoreService.findByGame(gameId).stream().map(ScoreDto::new).toList()).build();

    }

    @Override
    public Response getScoreByScorePoints(double points) {
            return Response.ok().entity(scoreService.findByScorePoints(points).stream().map(ScoreDto::new).toList()).build();
    }

    //Only temporary for testing
    @Override
    public Response createScore(CreateScoreCommand score) {
        return scoreService.saveScore(score.userId(),score.gameId(),score.scorePoints()).map(ScoreDto::new)
                .map(score1 -> Response.status(Response.Status.CREATED).entity(score1))
                .orElseGet(() ->  Response.status(Response.Status.NOT_FOUND)).build();
    }


}
