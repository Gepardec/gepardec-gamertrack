package com.gepardec.rest.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static com.gepardec.rest.api.HealthResource.BASE_HEALTH_PATH;


@Path(BASE_HEALTH_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface HealthResource {
    public static final String BASE_HEALTH_PATH = "health";


    @Operation(summary = "Get the App health status", description = "Returns App Health")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "App is running!", content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            @ApiResponse(responseCode = "503", description = "App is starting or dependencies are unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON))
    })
    @GET
    public Response healthCheck();

}
