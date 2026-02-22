package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.enums.VacationsStatusEnum;
import ar.edu.itba.paw.webapp.controller.AppointmentController;
import ar.edu.itba.paw.webapp.controller.DoctorController;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.InsuranceController;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class DoctorDTO {
    private String email;
    private String name;
    private String telephone;
    private String license;
    private String specialty;
    private String address;
    private String startTime;
    private String endTime;

    private LinkDTO links;

    public static Function<Doctor, DoctorDTO> mapper(final UriInfo uriInfo) {
        return d -> fromDoctor(uriInfo, d);
    }

    public static DoctorDTO fromDoctor(final UriInfo uriInfo, final Doctor doctor) {
        final DoctorDTO dto = new DoctorDTO();

        dto.email = doctor.getEmail();
        dto.name = doctor.getName();
        dto.telephone = doctor.getTelephone();
        dto.license = doctor.getLicence();
        dto.specialty = doctor.getSpecialty().toString();

        URI self = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctor.getId())).build();
        URI image = uriInfo.getBaseUriBuilder().path(FileController.class).path(String.valueOf(doctor.getPicture().getId())).build();
        URI schedule = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctor.getId())).path("shifts").build();
        URI insurances = uriInfo.getBaseUriBuilder().path(InsuranceController.class).queryParam("supportedBy", String.valueOf(doctor.getId())).build();
        URI freeAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class).queryParam("userId", String.valueOf(doctor.getId())).queryParam("status", AppointmentStatusEnum.FREE).queryParam("date", LocalDate.now()).build();
        URI futureAppointments = uriInfo.getBaseUriBuilder().path(AppointmentController.class).queryParam("userId", String.valueOf(doctor.getId())).queryParam("status", AppointmentStatusEnum.TAKEN).build();
        URI patients = uriInfo.getBaseUriBuilder().path(PatientController.class).queryParam("doctorId", String.valueOf(doctor.getId())).build();
        URI pastVacations = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctor.getId())).path("vacations").queryParam("status", VacationsStatusEnum.COMPLETED.getValue()).build();
        URI futureVacations = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctor.getId())).path("vacations").queryParam("status", VacationsStatusEnum.PROGRAMMED.getValue()).build();

        URI baseAuthorization = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(doctor.getId())).path("authorizations").build();
        TemplatedLinkDTO authorization = TemplatedLinkDTO.withQueryParams(baseAuthorization, List.of("patientId"));

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setImage(image)
            .setSchedule(schedule)
            .setInsurances(insurances)
            .setFreeAppointments(freeAppointments)
            .setFutureAppointments(futureAppointments)
            .setPatients(patients)
            .setAuthorization(authorization)
            .setPastVacations(pastVacations)
            .setFutureVacations(futureVacations)
        );

        return dto;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getLicense() {
        return license;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getAddress() {
        return address;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public LinkDTO getLinks() {
        return links;
    }

    // setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
