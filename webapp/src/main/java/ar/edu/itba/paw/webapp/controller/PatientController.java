package ar.edu.itba.paw.webapp.controller;

import java.math.BigDecimal;
import java.net.URI;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.webapp.dto.input.PatientCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientDTO;
import ar.edu.itba.paw.webapp.exception.NotFoundException;

@Path("/patients")
@Component
public class PatientController {
    
    @Autowired
    private PatientService ps;

    @Autowired
    private FileService is;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createPatient(@Valid PatientCreateDTO dto) {
        final Patient patient = ps.createPatient(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getTelephone(), LocaleEnum.fromLocale(LocaleContextHolder.getLocale()), dto.getBirthDate(), BigDecimal.valueOf(dto.getHeight()), BigDecimal.valueOf(dto.getWeight()));
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(patient.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response edit(
        @PathParam("id") long id,
        @Valid PatientEditDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        ps.updatePatient(patient, dto.getTelephone(), dto.getPictureId(), LocaleEnum.valueOf(dto.getMailLanguage()), dto.getBirthDate(), dto.getBloodtype(), BigDecimal.valueOf(dto.getHeight()), BigDecimal.valueOf(dto.getWeight()), dto.getSmokes(), dto.getDrinks(), dto.getMeds(), dto.getConditions(), dto.getAllergies(), dto.getDiet(), dto.getHobbies(), dto.getJob(), dto.getInsuranceId(), dto.getInsuranceNumber());
        return Response.ok().build();
    }
}
