package com.gepardec.rest.config.filters.response;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    protected static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, HEAD";
    protected static final String ALLOWED_HEADERS = "Content-Type, Authorization";
    protected static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED = "true";
    protected static final String ACCESS_CONTROL_EXPOSE_HEADERS =  "x-total-count, x-total-pages, x-page-size, x-current-page, Authorization";


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
            responseContext.getHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
            responseContext.getHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED);
            responseContext.getHeaders().add("Access-Control-Expose-Headers", ACCESS_CONTROL_EXPOSE_HEADERS);
        }
    }
}
