package com.gepardec.rest.impl;

import com.gepardec.core.services.SystemHealthService;
import com.gepardec.rest.api.HealthResource;
import com.gepardec.rest.model.dto.HealthStatusDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

public class HealthResourceImpl implements HealthResource {

    @Inject
    SystemHealthService systemHealthService;

    @Override
    public Response healthCheck() {
        boolean ready = systemHealthService.isReady();
        if (ready) {
            return Response.ok(new HealthStatusDto("UP", "Application is running")).build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new HealthStatusDto("DOWN", "Application is not ready"))
                .build();
    }
}
