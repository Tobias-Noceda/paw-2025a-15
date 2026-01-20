package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.enums.AppointmentStatus;
import ar.edu.itba.paw.webapp.dto.AppointmentDTO;


@Path("/appointments")
@Component
public class AppointmentController {

    @Autowired
    private AppointmentService as;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAppointments(
        @QueryParam("doctorId") Long doctorId,
        @QueryParam("status") String status,
        @QueryParam("date") String date
    ) {
        if (date == null || date.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing date parameter").build();
        } else if (doctorId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing doctorId parameter").build();
        } else {
            AppointmentStatus appointmentStatus;
            try {
                appointmentStatus = AppointmentStatus.fromString(status);
                List<AppointmentNew> appointments;
                
                switch (appointmentStatus) {
                    case Free -> {
                        appointments = as.getAvailableTurnsByDoctorIdByDate(doctorId, LocalDate.parse(date));
                    }
                    default -> {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported status").build();
                    }
                }

                final List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO.mapper(uriInfo))
                    .collect(Collectors.toList());
                return Response.ok(new GenericEntity<List<AppointmentDTO>>(appointmentDTOs) {}).build();
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status parameter").build();
            }
        }
    }
}
