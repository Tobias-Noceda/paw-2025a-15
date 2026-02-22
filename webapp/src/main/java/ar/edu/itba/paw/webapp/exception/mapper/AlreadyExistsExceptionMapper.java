package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;

@Provider
public class AlreadyExistsExceptionMapper implements ExceptionMapper<AlreadyExistsException>{

    @Override
    public Response toResponse(AlreadyExistsException exception) {
        return Response.status(Status.CONFLICT).build();
    }
    
}
