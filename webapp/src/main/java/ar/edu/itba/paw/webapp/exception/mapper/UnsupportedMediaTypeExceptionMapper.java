package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.models.exceptions.MediaTypeException;

@Provider
public class UnsupportedMediaTypeExceptionMapper implements ExceptionMapper<MediaTypeException>{
    
    @Override
    public Response toResponse(MediaTypeException exception) {
        return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
    }
}
