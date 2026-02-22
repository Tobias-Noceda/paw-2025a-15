package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.util.CacheHelper;
import ar.edu.itba.paw.webapp.controller.util.PaginationBuilder;
import ar.edu.itba.paw.webapp.dto.output.FileDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;

@Path("/files")
@Component
public class FileController {

    @Autowired
    private FileService fs;

    @Autowired
    private StudyService ss;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(VndType.APPLICATION_FILE)
    public Response listFiles(
        @QueryParam("studyId") final Long studyId,
        @QueryParam("page") @DefaultValue("1") final int page,
        @QueryParam("pageSize") @DefaultValue("10") Integer pageSize
    ) {
        Map<String, String> queryParams = new HashMap<>();

        if(studyId!=null) queryParams.put("studyId", studyId.toString());
            
        final List<FileDTO> files = ss.getStudyFilesPage(studyId, page, pageSize)
            .stream().map(FileDTO.mapper(uriInfo)).collect(Collectors.toList());
            
        return PaginationBuilder.buildResponse(
            Response.ok(new GenericEntity<List<FileDTO>>(files) {}),
            page, 
            pageSize, 
            ss.getStudyFilesCount(studyId), 
            queryParams, 
            uriInfo.getBaseUriBuilder().path(FileController.class)
        );
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createFile(
        @FormDataParam("file") InputStream fileStream,
        @FormDataParam("file") FormDataBodyPart filePart
    ) throws IOException {
        if (fileStream == null || filePart == null) return Response.status(Response.Status.BAD_REQUEST).build();
        String contentType = filePart.getMediaType().toString();
        FileTypeEnum type = FileTypeEnum.fromString(contentType);
        File file = fs.create(IOUtils.toByteArray(fileStream), type);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(file.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id:\\d+}")
    @Produces({"image/png", "image/jpeg", "application/pdf"})
    public Response getFileById(
        @PathParam("id") final long id,
        @Context Request request
    ) {
        File file = fs.findById(id).orElseThrow(NotFoundException::new);

        EntityTag eTag = new EntityTag(file.getId().toString());
        ResponseBuilder builder = CacheHelper.evaluate(request, eTag);//check client
        if (builder != null) {
            CacheHelper.addCacheHeaders(builder, eTag);
            return builder.build();//304
        }
        //first time flow
        builder = Response.ok(file.getContent())
            .type(file.getType().getName())
            .header("Content-Disposition",
                    "inline; filename=\"file_" + file.getId() + file.getType().getExtension() + "\"")
            .header("Content-Security-Policy", "frame-ancestors 'self' http://localhost:5173");

        CacheHelper.addCacheHeaders(builder, eTag);
        
        return builder.build();
    }
}
