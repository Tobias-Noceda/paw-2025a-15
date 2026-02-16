package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.webapp.controller.FileController;


public class FileDTO {
    private String type;
    
    private LinkDTO links;

    public static Function<File, FileDTO> mapper(final UriInfo uriInfo){
        return (f) -> fromFile(uriInfo, f);
    }

    public static FileDTO fromFile(final UriInfo uriInfo, File file){
        final FileDTO dto = new FileDTO();

        dto.setType(file.getType().getName());

        URI baseSelf = uriInfo.getBaseUriBuilder().path(FileController.class).path(String.valueOf(file.getId())).build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);
       
        dto.setLinks(new LinkDTO()
            .setSelf(self)
        );

        return dto;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
