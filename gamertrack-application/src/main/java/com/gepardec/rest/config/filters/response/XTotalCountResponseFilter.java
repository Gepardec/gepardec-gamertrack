package com.gepardec.rest.config.filters.response;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Collection;

@Provider
public class XTotalCountResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        var body = responseContext.getEntity();

        if (!responseContext.getHeaders().containsKey("X-Total-Count") && body instanceof Collection) {
            responseContext.getHeaders().add("X-Total-Count", ((Collection<?>) body).size());
        }

    }
}
