package ar.edu.itba.paw.webapp.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.VacationsStatusEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.webapp.controller.util.AuthenticatedUser;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.input.VacationCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.DoctorAuthorizationUpdateDTO;
import ar.edu.itba.paw.webapp.dto.input.DoctorCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.ShiftsModificationDTO;
import ar.edu.itba.paw.webapp.dto.output.DoctorAuthorizationDTO;
import ar.edu.itba.paw.webapp.dto.output.DoctorDTO;
import ar.edu.itba.paw.webapp.dto.output.DoctorVacationDTO;
import ar.edu.itba.paw.webapp.dto.output.ShiftDTO;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/doctors")
@Component
public class DoctorController {

    @Autowired
    private AuthDoctorService ads;

    @Autowired
    private AppointmentService as;

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
    @Produces(value = VndType.APPLICATION_DOCTOR)
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
            uriInfo.getBaseUriBuilder().path(DoctorController.class)
        );
    }

    @POST
    @Consumes(value = VndType.APPLICATION_DOCTOR_CREATION)
    public Response createDoctor(
        @Valid DoctorCreateDTO doctorCreateDTO
    ) {
        ShiftsModificationDTO shiftsModificationDTO = doctorCreateDTO.getShifts();
        if (shiftsModificationDTO != null && shiftsModificationDTO.getEndTime().isBefore(shiftsModificationDTO.getStartTime())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Shift end time cannot be before start time").build();
        }
        List<Insurance> insurances = doctorCreateDTO.getInsurances().stream()
            .map(name -> {//TODO pasar logica al service
                return is.getInsuranceByName(name).orElseThrow(() -> new NotFoundException("Insurance with name: " + name + " does not exist!"));
            }).collect(Collectors.toList());

        Doctor doctor = ds.createDoctor(
            doctorCreateDTO.getEmail(),
            doctorCreateDTO.getPassword(),
            doctorCreateDTO.getName(),
            doctorCreateDTO.getTelephone(),
            doctorCreateDTO.getLicense(),
            doctorCreateDTO.getSpecialty(),
            insurances.stream().map((insurance) -> insurance.getId()).collect(Collectors.toList()),
            LocaleEnum.fromLocale(LocaleContextHolder.getLocale())
        );

        if (shiftsModificationDTO != null) {
            try {
                List<WeekdayEnum> weekdays = shiftsModificationDTO.getWeekdays() != null ? shiftsModificationDTO.getWeekdays() : List.of();
                dss.createShifts(
                    doctor.getId(),
                    weekdays,
                    shiftsModificationDTO.getAddress(),
                    shiftsModificationDTO.getStartTime(),
                    shiftsModificationDTO.getEndTime(),
                    shiftsModificationDTO.getDuration()
                );
            } catch (Exception e) {
                // If shift creation fails, we can choose to either delete the created doctor or just return an error response.
                // For now, we'll just return an error response.
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Doctor created but failed to create shifts").build();
            }
        }

        return Response.created(
            uriInfo.getBaseUriBuilder()
                .path(DoctorController.class)
                .path(doctor.getId().toString())
                .build()
        ).build();
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(value = VndType.APPLICATION_DOCTOR)
    public Response getDoctorById(@PathParam("id") Integer doctorId) {
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(NotFoundException::new);
        return Response.ok(DoctorDTO.fromDoctor(uriInfo, doctor)).build();
    }

    /*========================= SHIFTS =========================*/

    @GET
    @Path("/{id:\\d+}/shifts")
    @Produces(value = VndType.APPLICATION_DOCTOR_SHIFT)
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

    @PUT
    @Path("/{id:\\d+}/shifts")
    @Produces(value = VndType.APPLICATION_DOCTOR_SHIFT)
    public Response replaceShifts(
        @PathParam("id") Integer doctorId,
        @Valid ShiftsModificationDTO shiftsModificationDTO
    ) {
        dss.createShifts(
            doctorId,
            shiftsModificationDTO.getWeekdays(),
            shiftsModificationDTO.getAddress(),
            shiftsModificationDTO.getStartTime(),
            shiftsModificationDTO.getEndTime(),
            shiftsModificationDTO.getDuration()
        );

        return Response.created(uriInfo.getBaseUriBuilder()
            .path(DoctorController.class)
            .path(doctorId.toString())
            .path("shifts")
            .build()
        ).build();
    }

    /*========================= AUTHORIZATIONS =========================*/
    @GET
    @Path("/{id:\\d+}/authorizations")
    @Produces(value = VndType.APPLICATION_DOCTOR_AUTHORIZATION)
    public Response doctorAuthorizations(
        @PathParam("id") Integer doctorId,
        @QueryParam("patientId") Long patientId
    ) {
        try {
            User user = AuthenticatedUser.get();
            
            if (!user.getId().equals(patientId)) {//TODO etsa logica no deberia estar en auth?
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            
            if (!ads.hasAuthDoctor(user.getId(), doctorId)) {//TODO mismo en auth?
                return Response.ok(new GenericEntity<DoctorAuthorizationDTO>(new DoctorAuthorizationDTO(false, List.of())) {}).build();
            }
            
            return Response.ok(new GenericEntity<DoctorAuthorizationDTO>(
                new DoctorAuthorizationDTO(true, ads.getAuthAccessLevelEnums(user.getId(), doctorId))
            ) {}).build();
        } catch (Exception e) {
            System.out.println("Error fetching doctor authorizations: " + e.getMessage());
            System.out.println("Exception class: " + e.getClass().getName());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/{id:\\d+}/authorizations")
    @Consumes(value = VndType.APPLICATION_DOCTOR_AUTHORIZATION)
    public Response replaceDoctorAuthorizations(
        @PathParam("id") Integer doctorId,
        @Valid DoctorAuthorizationUpdateDTO doctorAuthorizationUpdateDTO
    ) {
        User loggedUser = AuthenticatedUser.get();

        if (loggedUser == null) {//TODO logica en auth??
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (
            (doctorAuthorizationUpdateDTO.isAuthorized() && !ads.hasAuthDoctor(loggedUser.getId(), doctorId)) ||
            (!doctorAuthorizationUpdateDTO.isAuthorized() && ads.hasAuthDoctor(loggedUser.getId(), doctorId))
        ) {
            ads.toggleAuthDoctor(loggedUser.getId(), doctorId);
        }

        if (doctorAuthorizationUpdateDTO.isAuthorized()) {
            ads.updateAuthDoctor(loggedUser.getId(), doctorId, doctorAuthorizationUpdateDTO.getAccessLevels());
        }

        return Response.ok().build();
    }

    @GET
    @Path("/{id:\\d+}/vacations")
    @Produces(value = VndType.APPLICATION_DOCTOR_VACATIONS)
    public Response listVacations(
        @PathParam("id") Long doctorId,
        @QueryParam("status") final String status,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        if (doctorId == null || ds.getDoctorById(doctorId).isEmpty() || status == null){ //TODO capaz en service ekl check?
            throw new NotFoundException();
        }
        VacationsStatusEnum statusEnum;
        try {
            statusEnum = VacationsStatusEnum.fromValue(status.toLowerCase());
        }
        catch(IllegalArgumentException e){
            return Response.status(Status.BAD_REQUEST).entity("Invalid Status value").build();
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("status", status);

        List<DoctorVacationDTO> vacations;
        int vacationsCount;

        switch (statusEnum) {
            case PROGRAMMED -> {
                vacations = ds.getDoctorVacationsFuturePage(doctorId, page, pageSize)
                .stream()
                .map(DoctorVacationDTO.mapper(uriInfo))
                .collect(Collectors.toList());

                vacationsCount = ds.getDoctorVacationsFutureCount(doctorId);
            }
            case COMPLETED -> {
                vacations = ds.getDoctorVacationsPastPage(doctorId, page, pageSize)
                .stream()
                .map(DoctorVacationDTO.mapper(uriInfo))
                .collect(Collectors.toList());

                vacationsCount = ds.getDoctorVacationsPastCount(doctorId);
            }
            default -> {
                return Response.status(Status.BAD_REQUEST).entity("Invalid Status value").build();
            }
        }
    
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<DoctorVacationDTO>>(vacations) {}), 
            page, 
            pageSize, 
            vacationsCount, 
            queryParams, 
            uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctorId)).path("vacations")
        );
    }

    @POST
    @Path("/{id:\\d+}/vacations")
    @Consumes(value = VndType.APPLICATION_DOCTOR_VACATIONS)
    public Response createVacation(
        @PathParam("id") Long doctorId,
        @Valid VacationCreateDTO vacationDTO
    ) {
        if (doctorId == null || ds.getDoctorById(doctorId).isEmpty()) {
            throw new NotFoundException();
        }

        LocalDate startDate = vacationDTO.getStartDate();
        LocalDate endDate = vacationDTO.getEndDate();

        //TODO toda esta logica en el service o en otra forma
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"End date must be after start date\"}")
                .build();
        }

        if (startDate.isBefore(LocalDate.now())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Start date cannot be in the past\"}")
                .build();
        }


        if (ds.vacationExists(doctorId, startDate, endDate)) {
            return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\": \"Vacation already exists for these dates\"}")
                .build();
        }

        DoctorVacation vacation = ds.createDoctorVacation(doctorId, startDate, endDate);

        if (vacationDTO.getCancelAppointments()) {
            as.cancelAppointmentRange(doctorId, startDate, endDate);
        }

        DoctorVacationDTO responseDTO = DoctorVacationDTO.fromVacation(uriInfo, vacation);

        URI location = uriInfo.getAbsolutePathBuilder()
            .queryParam("startDate", startDate.toString())
            .queryParam("endDate", endDate.toString())
            .build();

        return Response.created(location).entity(responseDTO).build();
    }


    @DELETE
    @Path("/{id:\\d+}/vacations/{startDate:\\d{4}-\\d{2}-\\d{2}}/{endDate:\\d{4}-\\d{2}-\\d{2}}")
    public Response deleteVacation(
        @PathParam("id") Long doctorId,
        @PathParam("startDate") String startDateStr,
        @PathParam("endDate") String endDateStr
    ) {
        if (doctorId == null || ds.getDoctorById(doctorId).isEmpty()) {//TODO en auth?
            throw new NotFoundException();
        }

        if (startDateStr == null || endDateStr == null) {
            return Response.status(Response.Status.BAD_REQUEST)//TODO aca??????
                .entity("{\"error\": \"startDate and endDate query parameters are required\"}")
                .build();
        }

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        ds.deleteDoctorVacation(doctorId, startDate, endDate);

        return Response.noContent().build();
    }
}
