package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.webapp.dto.output.PatientDTO;
import ar.edu.itba.paw.webapp.exception.NotFoundException;

@Path("/patients")
@Component
public class PatientController {
    
    @Autowired
    private PatientService ps;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientDTO.fromPatient(uriInfo, patient)).build();
    }
}
