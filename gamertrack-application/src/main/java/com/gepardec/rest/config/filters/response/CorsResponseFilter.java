package com.gepardec.rest.config.filters.response;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    protected static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, HEAD";
    protected static final String ALLOWED_HEADERS = "Content-Type, Authorization";
    protected static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED = "true";
    protected static final String ACCESS_CONTROL_EXPOSE_HEADERS =  "x-total-count, x-total-pages, x-page-size, x-current-page, Authorization";


    @ConfigProperty(name = "allowed.origins.as.regex")
    String matchingRegex;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String origin = requestContext.getHeaders().getFirst("Origin");

        if (origin != null && origin.matches(matchingRegex)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
            responseContext.getHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", ACCESS_CONTROL_ALLOW_CREDENTIALS_IS_ALLOWED);
            responseContext.getHeaders().add("Access-Control-Expose-Headers", ACCESS_CONTROL_EXPOSE_HEADERS);
        }
    }
}
