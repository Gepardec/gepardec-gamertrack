package com.gepardec.rest.mapper;

import com.gepardec.impl.GameDoesNotExistException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GameDoesNotExistExceptionMapper implements ExceptionMapper<GameDoesNotExistException> {

  @Override
  public Response toResponse(GameDoesNotExistException exception) {

    return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
  }
}
