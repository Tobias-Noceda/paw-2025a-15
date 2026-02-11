package ar.edu.itba.paw.webapp.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;

@Provider
public class AppointmentAlreadyTakenExceptionMapper implements ExceptionMapper<AppointmentAlreadyTakenException>{

    @Override
    public Response toResponse(AppointmentAlreadyTakenException exception) {
        return Response.status(Status.CONFLICT).build();
    }
    
}
