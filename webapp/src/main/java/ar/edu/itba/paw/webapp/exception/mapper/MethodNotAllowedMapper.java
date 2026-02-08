package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MethodNotAllowedMapper implements ExceptionMapper<NotAllowedException> {

    @Override
    public Response toResponse(NotAllowedException exception) {
        if (exception instanceof NotAllowedException) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        }
        
        throw exception;
    }
    
}
