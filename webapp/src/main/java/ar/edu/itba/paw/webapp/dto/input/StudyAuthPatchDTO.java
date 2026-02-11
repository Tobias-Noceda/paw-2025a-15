package ar.edu.itba.paw.webapp.dto.input;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

public class StudyAuthPatchDTO {
    // TODO: Validate that these are valid doctor URNs
    private List<@URL(protocol = "http") String> doctors;
    
    @NotNull
    @NotBlank
    private Boolean authorize;

    public List<String> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<String> doctors) {
        this.doctors = doctors;
    }

    public Boolean isAuthorize() {
        return authorize;
    }

    public void setAuthorize(Boolean authorize) {
        this.authorize = authorize;
    }
}
