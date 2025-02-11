package com.gepardec.rest.config.filters.response;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    Dotenv dotenv = Dotenv
            .configure()
            .directory("../..")
            .filename("application.properties")
            .ignoreIfMissing()
            .load();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String origin = requestContext.getHeaders().getFirst("Origin");

        if (origin != null && origin.matches(dotenv.get("ALLOWED_ORIGINS_AS_REGEX"))) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", true);
        }
    }
}
