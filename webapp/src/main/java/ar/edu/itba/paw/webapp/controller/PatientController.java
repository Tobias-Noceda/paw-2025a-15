package ar.edu.itba.paw.webapp.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
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
import ar.edu.itba.paw.webapp.exception.NotFoundException;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/patients")
@Component
public class PatientController {
    
    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Autowired
    private StudyService ss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = VndType.APPLICATION_PATIENT)
    public Response listPatients(
        @QueryParam("doctorId") final Long doctorId,
        @QueryParam("name") final String name,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        if(name!=null) queryParams.put("name", name);

        if (doctorId != null) queryParams.put("doctorId", doctorId.toString());

        final List<PatientDTO> patients = ds.getAuthPatientsPageByDoctorIdAndName(doctorId, name, page, pageSize)
            .stream().map(PatientDTO.mapper(uriInfo)).collect(Collectors.toList());

        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<PatientDTO>>(patients) {}),
            page, 
            pageSize, 
            ds.getAuthPatientsCountByDoctorIdAndName(doctorId, name), 
            queryParams, 
            uriInfo
        );
    }

    @POST
    @Consumes(value = VndType.APPLICATION_PATIENT)
    public Response createPatient(@Valid PatientCreateDTO dto) {
        final Patient patient = ps.createPatient(
            dto.getEmail(), dto.getPassword(), 
            dto.getName(), dto.getTelephone(), 
            LocaleEnum.fromLocale(LocaleContextHolder.getLocale()), 
            dto.getBirthDate(), BigDecimal.valueOf(dto.getHeight()), 
            BigDecimal.valueOf(dto.getWeight())
        );
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(patient.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getPatientById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response editPatient(
        @PathParam("id") long id,
        @Valid PatientEditDTO dto
    ) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        ps.updatePatient(
            patient, 
            dto.getTelephone(), 
            dto.getPictureId(), 
            dto.getMailLanguage()!=null?LocaleEnum.valueOf(dto.getMailLanguage()):null, 
            dto.getBirthDate(), dto.getBloodtype(), 
            BigDecimal.valueOf(dto.getHeight()), 
            BigDecimal.valueOf(dto.getWeight()), 
            null, null, null, null, null, null, null, null, 
            dto.getInsuranceId(), 
            dto.getInsuranceNumber()
        );
        return Response.ok().build();
    }

    /*========================= INFO =========================*/

    @GET
    @Path("/{id:\\d+}/medicalInfo")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getPatientMedicalInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientMedicalInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/medicalInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
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
        return Response.ok().build();
    }

    @GET
    @Path("/{id:\\d+}/socialInfo")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getPatientSocialInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientSocialInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/socialInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
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
        return Response.ok().build();
    }

    @GET
    @Path("/{id:\\d+}/habitsInfo")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getPatientHabitsInfoById(@PathParam("id") final long id) {
        Patient patient = ps.getPatientById(id).orElseThrow(NotFoundException::new);
        return Response.ok(PatientHabitsInfoDTO.fromPatient(uriInfo, patient)).build();
    }

    @PATCH
    @Path("/{id:\\d+}/habitsInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
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
        return Response.ok().build();
    }

    /*========================= STUDIES =========================*/

    @GET
    @Path("/{id:\\d+}/studies")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response listStudies(
        @PathParam("id") final long id,
        @QueryParam("doctorId") final Long doctorId,
        @QueryParam("studyType") final String studyType,
        @QueryParam("recent") @DefaultValue("true") final Boolean recent,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        StudyTypeEnum type = null;
        if(studyType != null) {
            queryParams.put("studyType", studyType);
            type = StudyTypeEnum.fromDisplayName(studyType);
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
            uriInfo
        );
    }

    @POST
    @Path("/{id:\\d+}/studies")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createStudy(
        @PathParam("id") final long id,
        @Valid StudyCreateDTO dto
    ) {
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
            id,//TODO se podra obtener del auth capaz?
            dto.getStudyDate()
        );
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(study.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id:\\d+}/studies/{studyId:\\d+}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getStudyById(
        @PathParam("id") final long id,
        @PathParam("studyId") final long studyId
    ) {
        Study study = ss.getStudyById(studyId).orElseThrow(NotFoundException::new);
        return Response.ok(StudyDTO.fromStudy(uriInfo, study)).build();
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
