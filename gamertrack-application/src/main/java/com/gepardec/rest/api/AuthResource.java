package com.gepardec.rest.api;

import com.gepardec.rest.model.command.AuthCredentialCommand;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("auth")
public interface AuthResource {

    @POST
    @Path("/login")
    Response login(AuthCredentialCommand authCredentialCommand);
}
