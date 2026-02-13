package ar.edu.itba.paw.webapp.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.util.AuthenticatedUser;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.controller.util.URIHelper;
import ar.edu.itba.paw.webapp.dto.input.StudyCreateDTO;
import ar.edu.itba.paw.webapp.dto.output.StudyDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/studies")
@Component
public class StudyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyController.class);

    @Autowired
    private StudyService ss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response listStudies(
        @QueryParam("doctorId") final Long doctorId,
        @QueryParam("patientId") @NotNull final Long patientId,
        @QueryParam("studyType") final String studyType,
        @QueryParam("recent") @DefaultValue("true") final Boolean recent,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
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
        queryParams.put("patientId", patientId.toString());
        
        if(doctorId != null) queryParams.put("doctorId", doctorId.toString());
        
        List<StudyDTO> studies = ss.getFilteredStudiesPage(patientId, doctorId, type, recent, page, pageSize)
            .stream().map(StudyDTO.mapper(uriInfo)).collect(Collectors.toList());
        
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<StudyDTO>>(studies) {}),
            page, 
            pageSize, 
            ss.getFilteredStudiesCount(patientId, doctorId, type), 
            queryParams, 
            uriInfo.getBaseUriBuilder().path(StudyController.class)
        );
    }

    @POST
    @Consumes(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response createStudy(
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
            user.getId(),
            user.getId(),
            dto.getStudyDate()
        );
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(study.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{studyId:\\d+}")
    @Produces(value = VndType.APPLICATION_PATIENT_STUDY)
    public Response getStudyById(
        @PathParam("studyId") final long studyId
    ) {
        Study study = ss.getStudyById(studyId).orElseThrow(NotFoundException::new);
        return Response.ok(StudyDTO.fromStudy(uriInfo, study)).build();
    }

    @DELETE
    @Path("/{studyId:\\d+}")
    public Response deleteStudyById(
        @PathParam("studyId") final long studyId
    ) {
        ss.deleteStudy(studyId);
        return Response.noContent().build();
    }
}
