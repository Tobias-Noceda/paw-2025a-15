package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.models.exceptions.NotFoundException; 

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<RuntimeException>{

    @Override
    public Response toResponse(RuntimeException exception) {
        if (exception instanceof NotFoundException || 
            exception instanceof ar.edu.itba.paw.webapp.exception.NotFoundException) {
            return Response.status(Status.NOT_FOUND).build();
        }
        throw exception;
    }
    
}
