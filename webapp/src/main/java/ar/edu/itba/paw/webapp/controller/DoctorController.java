package ar.edu.itba.paw.webapp.controller;

import java.util.List;
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
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.webapp.dto.DoctorDTO;
import ar.edu.itba.paw.webapp.dto.ShiftDTO;

@Path("/doctors")
@Component
public class DoctorController {

    @Autowired
    private DoctorService ds;

    @Autowired
    private InsuranceService is;

    @Autowired
    private DoctorShiftService dss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listDoctors(
        @QueryParam("name") String name,
        @QueryParam("specialty") String specialtyName,
        @QueryParam("insurance") String insuranceName,
        @QueryParam("weekday") String weekday,
        @QueryParam("orderBy") String orderBy,
        @QueryParam("page") @DefaultValue("1") Integer page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Insurance insurance = is.getInsuranceByName(insuranceName).orElse(null);
        SpecialtyEnum specialty = specialtyName != null ? SpecialtyEnum.valueOf(specialtyName) : null;
        WeekdayEnum weekdayEnum = weekday != null ? WeekdayEnum.valueOf(weekday) : null;
        DoctorOrderEnum doctorOrder = orderBy != null ? DoctorOrderEnum.valueOf(orderBy) : null;

        final List<DoctorDTO> doctors = ds.getDoctorsPageByParams(
            name,
            specialty,
            insurance != null ? insurance.getId() : null,
            weekdayEnum,
            doctorOrder,
            page,
            pageSize
        ).stream().map(DoctorDTO.mapper(uriInfo)).collect(Collectors.toList());

        System.out.println("Doctors: " + doctors);

        return Response.ok(
            new GenericEntity<List<DoctorDTO>>(doctors) {}
        ).build();
    }

    @GET
    @Path("/{id}/shifts")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listShifts(
        @PathParam("id") Integer doctorId,
        @QueryParam("page") @DefaultValue("1") Integer page,
        @QueryParam("pageSize") @DefaultValue("100") Integer pageSize
    ) {
        List<ShiftDTO> shifts = dss.getActiveShiftsByDoctorId(doctorId)
            .stream()
            .map(ShiftDTO.mapper(uriInfo))
            .collect(Collectors.toList());

        return Response.ok(
            new GenericEntity<List<ShiftDTO>>(shifts) {}
        ).build();
    }
}
