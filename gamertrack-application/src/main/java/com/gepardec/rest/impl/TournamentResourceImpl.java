package com.gepardec.rest.impl;

import com.gepardec.core.services.TournamentService;
import com.gepardec.rest.api.TournamentResource;
import com.gepardec.rest.model.command.AddTournamentParticipantCommand;
import com.gepardec.rest.model.command.CreateTournamentCommand;
import com.gepardec.rest.model.command.CreateTournamentMatchCommand;
import com.gepardec.rest.model.command.UpdateTournamentStateCommand;
import com.gepardec.rest.model.dto.MatchRestDto;
import com.gepardec.rest.model.dto.TournamentRestDto;
import com.gepardec.rest.model.dto.TournamentStandingRestDto;
import com.gepardec.rest.model.mapper.TournamentRestMapper;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class TournamentResourceImpl implements TournamentResource {

    private final Logger logger = LoggerFactory.getLogger(TournamentResourceImpl.class);

    @Inject
    private TournamentService tournamentService;

    @Inject
    private TournamentRestMapper restMapper;

    @Override
    public Response getTournaments() {
        logger.info("Getting all tournaments");

        return Response.ok()
                .entity(tournamentService.findAllTournaments().stream()
                        .map(TournamentRestDto::new)
                        .toList())
                .build();
    }

    @Override
    public Response getTournamentByToken(String token) {
        logger.info("Getting tournament with Token: %s".formatted(token));

        return tournamentService.findTournamentByToken(token)
                .map(TournamentRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }

    @Override
    public Response createTournament(CreateTournamentCommand tournamentCmd) {
        logger.info("Creating tournament: %s".formatted(tournamentCmd));

        return tournamentService
                .saveTournament(restMapper.createTournamentCommandToTournament(tournamentCmd))
                .map(TournamentRestDto::new)
                .map(tournament -> Response.status(Status.CREATED).entity(tournament))
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response updateTournamentState(String token, UpdateTournamentStateCommand stateCmd) {
        logger.info("Updating state of tournament with Token: %s to %s"
                .formatted(token, stateCmd.state()));

        return tournamentService.updateTournamentState(token, stateCmd.state())
                .map(TournamentRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response addParticipant(String token, AddTournamentParticipantCommand participantCmd) {
        logger.info("Adding participant %s to tournament with Token: %s"
                .formatted(participantCmd.userToken(), token));

        return tournamentService.addParticipant(token, participantCmd.userToken())
                .map(TournamentRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response removeParticipant(String token, String userToken) {
        logger.info("Removing participant %s from tournament with Token: %s"
                .formatted(userToken, token));

        return tournamentService.removeParticipant(token, userToken)
                .map(TournamentRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response createTournamentMatch(String token, CreateTournamentMatchCommand matchCmd) {
        logger.info("Creating match in tournament with Token: %s".formatted(token));

        return tournamentService
                .saveTournamentMatch(token, restMapper.usersFromTokens(matchCmd.userTokens()))
                .map(MatchRestDto::new)
                .map(match -> Response.status(Status.CREATED).entity(match))
                .orElseGet(() -> Response.status(Status.BAD_REQUEST)).build();
    }

    @Override
    public Response getTournamentMatches(String token) {
        logger.info("Getting matches of tournament with Token: %s".formatted(token));

        return tournamentService.findTournamentMatches(token)
                .map(matches -> matches.stream().map(MatchRestDto::new).toList())
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }

    @Override
    public Response getTournamentStandings(String token) {
        logger.info("Getting standings of tournament with Token: %s".formatted(token));

        return tournamentService.findStandings(token)
                .map(standings -> standings.stream().map(TournamentStandingRestDto::new).toList())
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }

    @Override
    public Response deleteTournament(String token) {
        logger.info("Deleting tournament with Token: %s".formatted(token));

        return tournamentService.deleteTournament(token)
                .map(TournamentRestDto::new)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Status.NOT_FOUND)).build();
    }
}
