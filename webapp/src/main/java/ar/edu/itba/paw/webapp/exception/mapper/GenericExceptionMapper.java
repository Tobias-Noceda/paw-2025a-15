package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable>{
    
    @Override
    public Response toResponse(Throwable exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}
