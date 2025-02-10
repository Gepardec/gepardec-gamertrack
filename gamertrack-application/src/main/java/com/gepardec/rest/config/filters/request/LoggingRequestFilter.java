package com.gepardec.rest.config.filters.request;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Provider
public class LoggingRequestFilter implements ContainerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(LoggingRequestFilter.class);


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb
                .append("\n\t\t%s".formatted(requestContext.getMethod()))
                .append(" ")
                .append(requestContext.getUriInfo().getPath());

        AtomicReference<String> tmp = new AtomicReference<>("");


        // PREPARE VERB AND PATH
        if (!requestContext.getUriInfo().getQueryParameters().isEmpty()) {
            requestContext.getUriInfo()
                    .getQueryParameters()
                    .forEach((key, value) ->
                    {
                        value.forEach(v -> tmp.set("&" + tmp + v));
                        tmp.set(tmp.get().replaceFirst("&", ""));
                        sb.append("?%s=%s".formatted(key, tmp.get()));
                        tmp.set("");
                    });
        }
        sb.append("\n");

        // PREPARE HEADERS

        requestContext
                .getHeaders()
                .forEach((k, v) ->
                {
                    v.forEach(value -> tmp.set(tmp + value));
                    sb.append("\t\t%s:\t%s\n".formatted(k, tmp.get()));
                    tmp.set("");
                });

        logger.info("Request logged: {}", sb);
    }
}
