package ar.edu.itba.paw.webapp.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.webapp.controller.util.AuthenticatedUser;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.controller.util.URIHelper;
import ar.edu.itba.paw.webapp.dto.input.PatientCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditHabitsInfoDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditMedicalInfoDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditSocialInfoDTO;
import ar.edu.itba.paw.webapp.dto.input.StudyCreateDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientHabitsInfoDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientMedicalInfoDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientSocialInfoDTO;
import ar.edu.itba.paw.webapp.dto.output.StudyDTO;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.input.StudyAuthPatchDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/patients")
@Component
public class PatientController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);

    private static final long MAIL_TOKEN_EXPIRY_TIME = TimeUnit.HOURS.toMillis(2);

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Autowired
    private StudyService ss;

    @Autowired
    private AuthStudiesService ass;

    @Autowired
    private JwtTokenUtil jtu;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = VndType.APPLICATION_PATIENT)
    public Response listPatients(
        @QueryParam("doctorId") @NotNull final Long doctorId,
        @QueryParam("name") final String name,
        @QueryParam("page") @DefaultValue("1") @Min(1) final int page,
        @QueryParam("pageSize") @DefaultValue("10") @Min(1) @Max(100) Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        queryParams.put("doctorId", doctorId.toString());

        if(name!=null) queryParams.put("name", name);

        final List<PatientDTO> patients = ds.getAuthPatientsPageByDoctorIdAndName(doctorId, name, page, pageSize)
            .stream().map(PatientDTO.mapper(uriInfo)).collect(Collectors.toList());

        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<PatientDTO>>(patients) {}),
            page, 
            pageSize, 
            ds.getAuthPatientsCountByDoctorIdAndName(doctorId, name), 
            queryParams, 
            uriInfo.getBaseUriBuilder().path(PatientController.class)
        );
    }

    @POST
    @Consumes(value = VndType.APPLICATION_PATIENT_CREATION)
    public Response createPatient(@Valid PatientCreateDTO dto) {
        String verifyToken = jtu.createVerifyToken(dto.getEmail(), MAIL_TOKEN_EXPIRY_TIME);
        
        final Patient patient = ps.createPatient(
            dto.getEmail(), dto.getPassword(), 
            dto.getName(), dto.getTelephone(), 
            LocaleEnum.fromLocale(LocaleContextHolder.getLocale()), 
            dto.getBirthdate(), BigDecimal.valueOf(dto.getHeight()), 
            BigDecimal.valueOf(dto.getWeight()), verifyToken
        );
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(patient.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(value = VndType.APPLICATION_PATIENT)
    public Response getPatientById(
        @PathParam("id") final long id
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}")
    @Consumes(value = VndType.APPLICATION_PATIENT)
    @Produces(value = VndType.APPLICATION_PATIENT)
    public Response editPatient(
        @PathParam("id") long id,
        @Valid PatientEditDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        Long pictureId = URIHelper.getId(
            dto.getPictureId(), 
            uriInfo.getBaseUriBuilder().path(FileController.class).build()
        );
        ps.updatePatient(
            patient, 
            dto.getTelephone(), 
            pictureId, 
            dto.getMailLanguage()!=null?LocaleEnum.valueOf(dto.getMailLanguage()):null, 
            dto.getBirthdate(), dto.getBloodType(), 
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
        return Response.ok(PatientDTO.fromPatient(uriInfo, patient)).build();
    }

    /*========================= INFO =========================*/

    @GET
    @Path("/{id:\\d+}/medicalInfo")
    @Produces(value = VndType.APPLICATION_PATIENT_MEDICALINFO)
    public Response getPatientMedicalInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientMedicalInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/medicalInfo")
    @Consumes(value = VndType.APPLICATION_PATIENT_MEDICALINFO)
    @Produces(value = VndType.APPLICATION_PATIENT_MEDICALINFO)
    public Response editPatientMedicalInfo(
        @PathParam("id") long id,
        @Valid PatientEditMedicalInfoDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        ps.updatePatient(
            patient, null, null, null, null, null, null, null, null, null, 
            dto.getMeds(), 
            dto.getConditions(), 
            dto.getAllergies(), 
            null, null, null, null, null
        );
        return Response.ok(PatientMedicalInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @GET
    @Path("/{id:\\d+}/socialInfo")
    @Produces(value = VndType.APPLICATION_PATIENT_SOCIALINFO)
    public Response getPatientSocialInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientSocialInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/socialInfo")
    @Consumes(value = VndType.APPLICATION_PATIENT_SOCIALINFO)
    @Produces(value = VndType.APPLICATION_PATIENT_SOCIALINFO)
    public Response editPatientSocialInfo(
        @PathParam("id") long id,
        @Valid PatientEditSocialInfoDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        ps.updatePatient(
            patient, null, null, null, null, null, null, null, null, null, null, null, null, 
            null, 
            dto.getHobbies(),
            dto.getJob(), null, null
        );
        return Response.ok(PatientSocialInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @GET
    @Path("/{id:\\d+}/habitsInfo")
    @Produces(value = VndType.APPLICATION_PATIENT_HABITSINFO)
    public Response getPatientHabitsInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientHabitsInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/habitsInfo")
    @Consumes(value = VndType.APPLICATION_PATIENT_HABITSINFO)
    @Produces(value = VndType.APPLICATION_PATIENT_HABITSINFO)
    public Response editPatientHabitsInfo(
        @PathParam("id") long id,
        @Valid PatientEditHabitsInfoDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        ps.updatePatient(
            patient, null, null, null, null, null, null, null,
            dto.getSmokes(), 
            dto.getDrinks(), 
            null, null, null, 
            dto.getDiet(), 
            null, null, null, null
        );
        return Response.ok(PatientHabitsInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    /*========================= STUDIES =========================*/

    @GET
    @Path("/{id:\\d+}/studies")
    @Produces(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response listStudies(
        @PathParam("id") final long id,
        @QueryParam("doctorId") final Long doctorId,
        @QueryParam("studyType") final String studyType,
        @QueryParam("recent") @DefaultValue("true") final Boolean recent,
        @QueryParam("page") @DefaultValue("1") @Min(1) final int page,
        @QueryParam("pageSize") @DefaultValue("10") @Min(1) @Max(100) Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();
        
        StudyTypeEnum type = null;
        try {
            type = StudyTypeEnum.fromDisplayName(studyType);
        } catch (Exception e) {
            LOGGER.info("Invalid study type filter: {}", studyType);
        }
        if(studyType != null) {
            queryParams.put("studyType", studyType);
        }
        
        queryParams.put("recent", recent.toString());
        
        if(doctorId != null) queryParams.put("doctorId", doctorId.toString());
        
        List<StudyDTO> studies = ss.getFilteredStudiesPage(id, doctorId, type, recent, page, pageSize)
            .stream().map(StudyDTO.mapper(uriInfo)).collect(Collectors.toList());
        
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<StudyDTO>>(studies) {}),
            page, 
            pageSize, 
            ss.getFilteredStudiesCount(id, doctorId, type), 
            queryParams, 
            uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(id)).path("studies")
        );
    }

    @POST
    @Path("/{id:\\d+}/studies")
    @Consumes(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response createStudy(
        @PathParam("id") final long id,
        @Valid StudyCreateDTO dto
    ) {
        User user = AuthenticatedUser.get();
        
        if (user == null) { //jjust in case, authConfig should have make sure of this already
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not authenticated").build();
        }

        final Study study = ss.create(
            StudyTypeEnum.fromDisplayName(dto.getType()), 
            dto.getComment(), 
            URIHelper.getIds(
                dto.getFiles(), 
                uriInfo.getBaseUriBuilder()
                    .path(FileController.class)
                    .build()
            ), 
            id, 
            user.getId(),
            dto.getStudyDate()
        );

        if (dto.getAuthorizedDoctors() != null) {
            try {
                ass.authStudyForDoctorIdList(
                    URIHelper.getIds(
                        dto.getAuthorizedDoctors(),
                        uriInfo.getBaseUriBuilder()
                            .path(DoctorController.class)
                            .build()
                    ),
                    study.getId()
                );
            } catch (NotFoundException nfe) {
                LOGGER.warn(nfe.getMessage());
            }
        }

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(study.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id:\\d+}/studies/{studyId:\\d+}")
    @Produces(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response getStudyById(
        @PathParam("id") final long id,
        @PathParam("studyId") final long studyId
    ) {
        Study study = ss.getStudyById(studyId).orElseThrow(NotFoundException::new);
        return Response.ok(StudyDTO.fromStudy(uriInfo, study)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/studies/{studyId:\\d+}")
    @Consumes(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response editStudyById(
        @PathParam("id") final long id,
        @PathParam("studyId") final long studyId,
        @Valid StudyAuthPatchDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
        Study study = ss.getStudyById(studyId).orElseThrow(() -> new NotFoundException("Study not found"));

        if (!study.getPatient().getId().equals(patient.getId())) {
            throw new NotFoundException("Study not found for the given patient");
        }

        if (dto.isAuthorize()) {
            ass.authStudyForDoctorIdList(
                URIHelper.getIds(
                    dto.getDoctors(),
                    uriInfo.getBaseUriBuilder()
                        .path(DoctorController.class)
                        .build()
                ),
                study.getId()
            );
        } else {
            ass.unauthStudyForDoctorIdList(
                URIHelper.getIds(
                    dto.getDoctors(),
                    uriInfo.getBaseUriBuilder()
                        .path(DoctorController.class)
                        .build()
                ),
                study.getId()
            );
        }

        return Response.accepted().build();
    }

    @DELETE
    @Path("/{id:\\d+}/studies/{studyId:\\d+}")
    public Response deleteStudyById(
        @PathParam("id") final long id,
        @PathParam("studyId") final long studyId
    ) {
        ss.deleteStudy(studyId);
        return Response.noContent().build();
    }
}
