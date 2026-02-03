package ar.edu.itba.paw.webapp.controller;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.dto.input.PatientEditDTO;
import ar.edu.itba.paw.webapp.dto.output.ProfileDTO;
import ar.edu.itba.paw.webapp.exception.NotFoundException;

@Path("/profile")
@Component
public class ProfileController {

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProfile() {
        User user = getAuthenticatedUser();
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (UserRoleEnum.PATIENT.equals(user.getRole())) {
            Patient patient = ps.getPatientById(user.getId()).orElseThrow(NotFoundException::new);
            return Response.ok(ProfileDTO.fromPatient(uriInfo, patient)).build();
        }
        if (UserRoleEnum.DOCTOR.equals(user.getRole())) {
            Doctor doctor = ds.getDoctorById(user.getId()).orElseThrow(NotFoundException::new);
            return Response.ok(ProfileDTO.fromDoctor(uriInfo, doctor)).build();
        }

        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateProfile(@Valid PatientEditDTO dto) {
        User user = getAuthenticatedUser();
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!UserRoleEnum.PATIENT.equals(user.getRole())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Patient patient = ps.getPatientById(user.getId()).orElseThrow(NotFoundException::new);
        ps.updatePatient(
            patient,
            dto.getTelephone(),
            dto.getPictureId(),
            dto.getMailLanguage() != null ? LocaleEnum.valueOf(dto.getMailLanguage()) : null,
            dto.getBirthDate(),
            dto.getBloodtype(),
            dto.getHeight() != null ? BigDecimal.valueOf(dto.getHeight()) : null,
            dto.getWeight() != null ? BigDecimal.valueOf(dto.getWeight()) : null,
            dto.getSmokes(),
            dto.getDrinks(),
            dto.getMeds(),
            dto.getConditions(),
            dto.getAllergies(),
            dto.getDiet(),
            dto.getHobbies(),
            dto.getJob(),
            dto.getInsuranceId(),
            dto.getInsuranceNumber()
        );
        return Response.ok().build();
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof PawAuthUserDetails details)) {
            return null;
        }

        return details.getUser();
    }
}
