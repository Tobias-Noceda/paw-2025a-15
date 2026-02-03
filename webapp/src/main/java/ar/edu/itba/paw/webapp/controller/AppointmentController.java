package ar.edu.itba.paw.webapp.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.AppointmentNewId;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.exceptions.AppointmentAlreadyTakenException;
import ar.edu.itba.paw.models.utils.Pair;
import ar.edu.itba.paw.webapp.controller.util.AuthenticatedUser;
import ar.edu.itba.paw.webapp.controller.util.DatePaginationBuilder;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.input.AppointmentEditDTO;
import ar.edu.itba.paw.webapp.dto.output.AppointmentDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;


@Path("/appointments")
@Component
public class AppointmentController {

    @Autowired
    private AppointmentService as;

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    private static final ZoneId ARGENTINA_ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    @GET
    @Produces(value = VndType.APPLICATION_APPOINTMENT)
    public Response listAppointments(
        @QueryParam("userId") Long userId,
        @QueryParam("status") @NotBlank String status,
        @QueryParam("date") String date,
        @QueryParam("page") @DefaultValue("1") int page,
        @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        AppointmentStatusEnum appointmentStatus = AppointmentStatusEnum.fromString(status);
        List<AppointmentDTO> appointmentDTOs;
        Integer totalItems;
        LocalDate today = LocalDate.now(ARGENTINA_ZONE);
        
        switch (appointmentStatus) {
            case FREE -> {
                if (userId == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("userId parameter is required for FREE status").build();
                }
                LocalDate selectedDate = date != null ? LocalDate.parse(date) : today;
                appointmentDTOs = as.getAvailableTurnsByDoctorIdByDate(userId, selectedDate)
                    .stream()
                    .map(AppointmentDTO.mapper(appointmentStatus, uriInfo))
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
                try {
                    if (userId != null) {   
                        appointmentDTOs = as.getFutureAppointmentDataPageByUserId(userId, page, pageSize)
                            .stream()
                            .map(AppointmentDTO.mapper(appointmentStatus, uriInfo))
                            .collect(Collectors.toList());
                        totalItems = as.getFutureAppointmentTotalByUserId(userId);
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST).entity("userId parameter is required for TAKEN status").build();
                    }
                } catch (Exception e) {
                    System.out.println("Error fetching TAKEN appointments: " + e.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching TAKEN appointments").build();
                }
            }
            case COMPLETED -> {
                if (userId != null) {
                    appointmentDTOs = as.getOldAppointmentDataPageByPatientId(userId, page, pageSize)
                        .stream()
                        .map(AppointmentDTO.mapper(appointmentStatus, uriInfo))
                        .collect(Collectors.toList());
                    totalItems = as.getOldAppointmentTotalByPatientId(userId);
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("userId parameter is required for COMPLETED status").build();
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

    @GET
    @Path("/{appointmentId}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAppointmentById(@PathParam("appointmentId") String appointmentId) {
        AppointmentNewId id = AppointmentNewId.fromId(appointmentId);
        Pair<AppointmentNew, AppointmentStatusEnum> appointmentPair = as.getAppointmentByShiftIdDateAndTime(id.getShiftId(), id.getDate(), id.getStartTime(), id.getEndTime());
        
        return Response.ok(new GenericEntity<AppointmentDTO>(
            AppointmentDTO.fromAppointment(appointmentPair.left(), appointmentPair.right(), uriInfo)
        ) {}).build();
    }

    @PATCH
    @Path("/{appointmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response modifyAppointment(
        @PathParam("appointmentId") String appointmentId,
        @Valid AppointmentEditDTO appointmentEditDTO
    ) {
        AppointmentNewId id = AppointmentNewId.fromId(appointmentId);

        Principal userPrincipal = securityContext.getUserPrincipal();

        User user = AuthenticatedUser.get(userPrincipal, email -> us.getUserByEmail(email).orElse(null));
        
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not authenticated").build();
        }

        AppointmentNew newAppointment;

        switch (appointmentEditDTO.getStatus()) {
            case TAKEN -> {
                if (appointmentEditDTO.getDescription() == null || appointmentEditDTO.getDescription().isBlank()) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Description is required when taking an appointment").build();
                }
                try {
                    newAppointment = as.addAppointment(id.getShiftId(), user.getId(), id.getDate(), id.getStartTime(), id.getEndTime(), appointmentEditDTO.getDescription());
                } catch (AppointmentAlreadyTakenException e) {
                    return Response.status(Response.Status.CONFLICT).entity("Appointment slot already taken").build();
                }
            }
            case FREE -> {
                newAppointment = as.cancelAppointment(id.getShiftId(), id.getDate(), id.getStartTime(), id.getEndTime(), user.getId());
            }
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported status for modification").build();
            }
        }

        return Response.ok(new GenericEntity<AppointmentDTO>(
            AppointmentDTO.fromAppointment(newAppointment, appointmentEditDTO.getStatus(), uriInfo)
        ) {}).build();
    }
}
