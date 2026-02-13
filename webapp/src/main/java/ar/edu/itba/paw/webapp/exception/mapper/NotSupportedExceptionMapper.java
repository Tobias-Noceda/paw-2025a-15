package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper implements ExceptionMapper<NotSupportedException>{
    
    @Override
    public Response toResponse(NotSupportedException exception) {
        return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
    }
}
