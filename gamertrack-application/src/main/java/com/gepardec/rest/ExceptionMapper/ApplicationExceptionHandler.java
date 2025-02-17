package com.gepardec.rest.ExceptionMapper;

import com.gepardec.foundation.exception.ApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<ApplicationException> {
    @Override
    public Response toResponse(ApplicationException exception) {
        if (exception instanceof ApplicationException.GameDoesNotExistException) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getErrorMessage()).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getErrorMessage()).build();
    }
}
