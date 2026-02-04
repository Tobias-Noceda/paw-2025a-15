package ar.edu.itba.paw.webapp.dto.output;

import java.util.List;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

public class DoctorAuthorizationDTO {
    
    private boolean authorized;
    private boolean medsAuthorized;
    private boolean socialAuthorized;
    private boolean habitsAuthorized;

    public DoctorAuthorizationDTO(boolean authorized, boolean medsAuthorized, boolean socialAuthorized, boolean habitsAuthorized) {
        this.authorized = authorized;
        this.medsAuthorized = medsAuthorized;
        this.socialAuthorized = socialAuthorized;
        this.habitsAuthorized = habitsAuthorized;
    }

    public DoctorAuthorizationDTO(boolean authorized, List<AccessLevelEnum> authorizationLevels) {
        this.authorized = authorized;
        this.medsAuthorized = authorizationLevels.contains(AccessLevelEnum.VIEW_MEDICAL);
        this.socialAuthorized = authorizationLevels.contains(AccessLevelEnum.VIEW_SOCIAL);
        this.habitsAuthorized = authorizationLevels.contains(AccessLevelEnum.VIEW_HABITS);
    }

    // Getters
    public boolean isAuthorized() {
        return authorized;
    }

    public boolean isMedsAuthorized() {
        return medsAuthorized;
    }

    public boolean isSocialAuthorized() {
        return socialAuthorized;
    }

    public boolean isHabitsAuthorized() {
        return habitsAuthorized;
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

    public void setMedsAuthorized(boolean medsAuthorized) {
        this.medsAuthorized = medsAuthorized;
    }

    public void setSocialAuthorized(boolean socialAuthorized) {
        this.socialAuthorized = socialAuthorized;
    }

    public void setHabitsAuthorized(boolean habitsAuthorized) {
        this.habitsAuthorized = habitsAuthorized;
    }
}
