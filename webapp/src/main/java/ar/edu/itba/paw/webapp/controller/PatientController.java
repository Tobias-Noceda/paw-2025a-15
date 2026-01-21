package ar.edu.itba.paw.webapp.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
import ar.edu.itba.paw.webapp.dto.input.PatientCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.PatientEditDTO;
import ar.edu.itba.paw.webapp.dto.output.PatientDTO;
import ar.edu.itba.paw.webapp.dto.output.StudyDTO;
import ar.edu.itba.paw.webapp.exception.NotFoundException;

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
    @Produces(value = MediaType.APPLICATION_JSON)
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
        ps.updatePatient(patient, dto.getTelephone(), dto.getPictureId(), dto.getMailLanguage()!=null?LocaleEnum.valueOf(dto.getMailLanguage()):null, dto.getBirthDate(), dto.getBloodtype(), BigDecimal.valueOf(dto.getHeight()), BigDecimal.valueOf(dto.getWeight()), dto.getSmokes(), dto.getDrinks(), dto.getMeds(), dto.getConditions(), dto.getAllergies(), dto.getDiet(), dto.getHobbies(), dto.getJob(), dto.getInsuranceId(), dto.getInsuranceNumber());
        return Response.ok().build();
    }

    /*========================= STUDIES =========================*/

    @GET
    @Path("/{id}/studies")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getStudiesById(
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
            type = StudyTypeEnum.valueOf(studyType);
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

    @GET
    @Path("/{id}/studies/{studyId}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getById(
        @PathParam("id") final long id,
        @PathParam("studyId") final long studyId
    ) {
        Study study = ss.getStudyById(studyId).orElseThrow(NotFoundException::new);
        return Response.ok(StudyDTO.fromStudy(uriInfo, study)).build();
    }
}
