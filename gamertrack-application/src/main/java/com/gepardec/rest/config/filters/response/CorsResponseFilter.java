package com.gepardec.rest.config.filters.response;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String origin = requestContext.getHeaders().getFirst("Origin");

        if (origin != null && origin.matches("^(http|https)://gamertrack-frontend.apps.cloudscale-lpg-2.appuio.cloud")) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        }
    }
}
