package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.webapp.controller.DoctorController;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class UserDTO {
    private LinkDTO links;

    public UserDTO() {
        // For Jersey
    }

    public UserDTO(LinkDTO links) {
        this.links = links;
    }

    public static Function<User, UserDTO> mapper(final UriInfo uriInfo) {
        return s -> fromUser(s, uriInfo);
    }

    public static UserDTO fromUser(User user, UriInfo uriInfo) {
        final UserDTO dto = new UserDTO();

        URI self;
        switch (user.getRole()) {
            case DOCTOR -> {
                self = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(user.getId())).build();
                break;
            }
            case PATIENT -> {
                self = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(user.getId())).build();
                break;
            }
            default -> {
                self = null;
                break;
            }
        }
        LinkDTO links = new LinkDTO();
        if (self != null) {
            links.setSelf(new TemplatedLinkDTO(self.toString(), false));
        }
        
        dto.links = links;

        return dto;
    }

    public LinkDTO getLinks() {
        return links;
    }

    public void setLinks(LinkDTO links) {
        this.links = links;
    }
}
