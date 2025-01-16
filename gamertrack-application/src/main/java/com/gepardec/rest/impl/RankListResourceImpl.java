package com.gepardec.rest.impl;

import com.gepardec.core.services.ScoreService;
import com.gepardec.rest.api.RankListResource;
import com.gepardec.rest.model.dto.ScoreRestDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class RankListResourceImpl implements RankListResource {
    @Inject
    private ScoreService scoreService;


    @Override
    public Response getTopScoresBygame(String gameToken, int top, Boolean includeDeactivated) {
        return scoreService.findTopScoresByGame(gameToken,top, includeDeactivated).stream().map(ScoreRestDto::new).toList().isEmpty()
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.ok().entity(scoreService.findTopScoresByGame(gameToken,top, includeDeactivated).stream().map(ScoreRestDto::new).toList()).build();
    }
}
