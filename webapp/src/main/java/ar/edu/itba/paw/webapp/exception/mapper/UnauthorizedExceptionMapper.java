package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException>{

    @Override
    public Response toResponse(UnauthorizedException exception) {
        return Response.status(Status.UNAUTHORIZED).build();
    }
    
}
