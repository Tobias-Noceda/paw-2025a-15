package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.DefaultValue;
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
import ar.edu.itba.paw.models.enums.AppointmentStatus;
import ar.edu.itba.paw.webapp.controller.util.DatePaginationBuilder;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.AppointmentDTO;


@Path("/appointments")
@Component
public class AppointmentController {

    @Autowired
    private AppointmentService as;

    @Context
    private UriInfo uriInfo;

    private static final ZoneId ARGENTINA_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAppointments(
        @QueryParam("doctorId") Long doctorId,
        @QueryParam("patientId") Long patientId,
        @QueryParam("status") @NotBlank String status,
        @QueryParam("date") String date,
        @QueryParam("page") @DefaultValue("1") int page,
        @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        AppointmentStatus appointmentStatus = AppointmentStatus.fromString(status);
        List<AppointmentDTO> appointmentDTOs;
        Integer totalItems;
        LocalDate today = LocalDate.now(ARGENTINA_ZONE);
        
        switch (appointmentStatus) {
            case FREE -> {
                if (doctorId == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("doctorId parameter is required for FREE status").build();
                }
                LocalDate selectedDate = date != null ? LocalDate.parse(date) : today;
                appointmentDTOs = as.getAvailableTurnsByDoctorIdByDate(doctorId, selectedDate)
                    .stream()
                    .map(AppointmentDTO.mapper(uriInfo))
                    .collect(Collectors.toList());

                return DatePaginationBuilder.buildResponse(
                    Response.ok(new GenericEntity<List<AppointmentDTO>>(appointmentDTOs) {}),
                    selectedDate,
                    today,
                    today.plusMonths(3),
                    uriInfo.getQueryParameters().entrySet().stream()
                        .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue().get(0)
                        )),
                    uriInfo
                );
            }
            case TAKEN -> {
                if (patientId != null) {
                    return Response.status(Response.Status.NOT_IMPLEMENTED).entity("Pagination for patient appointments not implemented yet").build();
                } else if (doctorId != null) {
                    appointmentDTOs = as.getFutureAppointmentDataPageByDoctorId(doctorId, page, pageSize)
                        .stream()
                        .map(AppointmentDTO.mapper(uriInfo))
                        .collect(Collectors.toList());
                    totalItems = as.getFutureAppointmentTotalByDoctorId(doctorId);
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Either patientId or doctorId parameter is required for TAKEN status").build();
                }
            }
            case COMPLETED -> {
                if (patientId != null) {
                    appointmentDTOs = as.getOldAppointmentDataPageByPatientId(patientId, page, pageSize)
                        .stream()
                        .map(AppointmentDTO.mapper(uriInfo))
                        .collect(Collectors.toList());
                    totalItems = as.getOldAppointmentTotalByPatientId(patientId);
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("patientId parameter is required for COMPLETED status").build();
                }
            }
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported status").build();
            }
        }
        
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<AppointmentDTO>>(appointmentDTOs) {}),
            page,
            pageSize,
            totalItems,
            uriInfo.getQueryParameters().entrySet().stream()
                .collect(Collectors.toMap(
                    e -> e.getKey(),
                    e -> e.getValue().get(0)
                )),
            uriInfo
        );
    }
}
