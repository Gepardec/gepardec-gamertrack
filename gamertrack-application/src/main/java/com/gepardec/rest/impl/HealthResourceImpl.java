package com.gepardec.rest.impl;

import com.gepardec.rest.api.HealthResource;
import jakarta.ws.rs.core.Response;

public class HealthResourceImpl implements HealthResource {

    @Override
    public Response getUsers() {
        return Response.ok("Application is running").build();
    }
}
