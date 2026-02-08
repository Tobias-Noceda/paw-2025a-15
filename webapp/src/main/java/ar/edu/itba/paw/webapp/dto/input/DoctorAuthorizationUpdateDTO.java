package ar.edu.itba.paw.webapp.dto.input;

import java.util.List;

import javax.validation.constraints.NotNull;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;

@NonEmptyBody
public class DoctorAuthorizationUpdateDTO {
    
    @NotNull
    private boolean authorized;

    @NotNull
    private List<AccessLevelEnum> accessLevels;

    public DoctorAuthorizationUpdateDTO() {
        // For Jersey
    }

    public DoctorAuthorizationUpdateDTO(boolean authorized, List<AccessLevelEnum> authorizationLevels) {
        this.authorized = authorized;
        this.accessLevels = authorizationLevels;
    }

    // Getters
    public boolean isAuthorized() {
        return authorized;
    }

    public List<AccessLevelEnum> getAccessLevels() {
        return accessLevels;
    }

    // Setters
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public void authorize() {
        this.authorized = true;
    }

    public void revokeAuthorization() {
        this.authorized = false;
    }

    public void setAccessLevels(List<AccessLevelEnum> accessLevels) {
        this.accessLevels = accessLevels;
    }
}
