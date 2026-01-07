package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.webapp.exception.FileNotFoundException;

@Provider
public class FileNotFoundExceptionMapper implements ExceptionMapper<FileNotFoundException>{

    @Override
    public Response toResponse(FileNotFoundException exception) {
        return Response.status(Status.NOT_FOUND).build();
    }
    
}
