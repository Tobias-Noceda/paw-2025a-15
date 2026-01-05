package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.webapp.dto.DoctorDTO;

@Path("/doctor")
@Component
public class DoctorController {

    @Autowired(required = false)
    private DoctorService ds;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listDoctors() {
        final List<DoctorDTO> doctors = ds.getDoctorsPageByParams(
            null,
            null,
            null,
            null,
            null,
            1,
            15
        ).stream().map(DoctorDTO::fromDoctor).collect(Collectors.toList());

        System.out.println("Doctors: " + doctors);

        return Response.ok(
            new GenericEntity<List<DoctorDTO>>(doctors) {

            }
        ).build();
    }
}
