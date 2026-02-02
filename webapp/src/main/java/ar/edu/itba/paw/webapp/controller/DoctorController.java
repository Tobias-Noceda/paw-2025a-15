package ar.edu.itba.paw.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.output.DoctorDTO;
import ar.edu.itba.paw.webapp.dto.output.ShiftDTO;
import ar.edu.itba.paw.webapp.exception.NotFoundException;

@Path("/doctors")
@Component
public class DoctorController {

    @Autowired
    private DoctorService ds;

    @Autowired
    private PatientService ps;

    @Autowired
    private StudyService ss;

    @Autowired
    private InsuranceService is;

    @Autowired
    private DoctorShiftService dss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listDoctors(
        @QueryParam("studyId") Long studyId,
        @QueryParam("patientId") Long patientId,
        @QueryParam("name") String name,
        @QueryParam("specialty") String specialtyName,
        @QueryParam("insurance") String insuranceName,
        @QueryParam("weekday") String weekday,
        @QueryParam("orderBy") String orderBy,
        @QueryParam("page") @DefaultValue("1") Integer page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        if (studyId != null) queryParams.put("studyId", studyId.toString());
        if (patientId != null) queryParams.put("patientId", patientId.toString());
        if (name != null) queryParams.put("name", name);
        if (specialtyName != null) queryParams.put("specialty", specialtyName);
        if (insuranceName != null) queryParams.put("insurance", insuranceName);
        if (weekday != null) queryParams.put("weekday", weekday);
        if (orderBy != null) queryParams.put("orderBy", orderBy);

        Insurance insurance = is.getInsuranceByName(insuranceName).orElse(null);
        SpecialtyEnum specialty = specialtyName != null ? SpecialtyEnum.valueOf(specialtyName) : null;
        WeekdayEnum weekdayEnum = weekday != null ? WeekdayEnum.valueOf(weekday) : null;
        DoctorOrderEnum doctorOrder = orderBy != null ? DoctorOrderEnum.valueOf(orderBy) : null;

        final List<DoctorDTO> doctors;
        final Integer totalDoctors;

        if (studyId != null) {
            doctors = ss.getAuthDoctorsPage(studyId, page, pageSize)
                .stream().map(DoctorDTO.mapper(uriInfo)).collect(Collectors.toList());
            totalDoctors = ss.getAuthDoctorsCount(studyId);
        }
        else if (patientId != null) {
            doctors = ps.getAuthDoctorsByPatientIdAndNamePage(patientId, name, page, pageSize)
                .stream().map(DoctorDTO.mapper(uriInfo)).collect(Collectors.toList());
            totalDoctors = ps.getAuthDoctorsByPatientIdAndNameCount(patientId, name);
        }
        else {
            doctors = ds.getDoctorsPageByParams(
                name,
                specialty,
                insurance != null ? insurance.getId() : null,
                weekdayEnum,
                doctorOrder,
                page,
                pageSize
            ).stream().map(DoctorDTO.mapper(uriInfo)).collect(Collectors.toList());

            totalDoctors = ds.getTotalDoctorsByParams(
                name,
                specialty,
                insurance != null ? insurance.getId() : null,
                weekdayEnum
            );
        }
         
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<DoctorDTO>>(doctors) {}),
            page,
            pageSize,
            totalDoctors,
            queryParams,
            uriInfo
        );
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getDoctorById(@PathParam("id") Integer doctorId) {
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(NotFoundException::new);
        return Response.ok(DoctorDTO.fromDoctor(uriInfo, doctor)).build();
    }

    /*========================= SHIFTS =========================*/

    @GET
    @Path("/{id:\\d+}/shifts")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listShifts(
        @PathParam("id") Integer doctorId
    ) {
        if (doctorId == null || ds.getDoctorById(doctorId).isEmpty()) {
            throw new NotFoundException();
        }

        List<ShiftDTO> shifts = dss.getActiveShiftsByDoctorId(doctorId)
            .stream().map(ShiftDTO.mapper(uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ShiftDTO>>(shifts) {}).build();
    }
}
