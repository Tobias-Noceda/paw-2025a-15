package ar.edu.itba.paw.webapp.dto.output;

import java.util.List;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

public class DoctorAuthorizationDTO {
    
    private boolean authorized;
    private List<AccessLevelEnum> accessLevels;

    public DoctorAuthorizationDTO() {
        // For Jersey
    }

    public DoctorAuthorizationDTO(boolean authorized, List<AccessLevelEnum> authorizationLevels) {
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
