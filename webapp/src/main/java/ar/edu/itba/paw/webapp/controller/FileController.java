package ar.edu.itba.paw.webapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.webapp.exception.FileNotFoundException;

@Path("/files")
@Component
public class FileController {

    @Autowired
    private FileService fs;

    @GET
    @Path("/{id}")
    @Produces(MediaType.WILDCARD)
    public Response getById(@PathParam("id") final long id) {
        File file = fs.findById(id).orElseThrow(FileNotFoundException::new);

        return Response.ok(file.getContent())
            .type(file.getType().getName())
            .header("Content-Disposition",
                    "inline; filename=\"file_" + file.getId() + file.getType().getExtension() + "\"")
            .build();
    }

}
