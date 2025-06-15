package ar.edu.itba.paw.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Controller
public class WebUserAuthDecision {

    @Autowired
    private StudyService ss;

    @Autowired
    private AuthDoctorService ads;

    @Autowired
    private AuthStudiesService ass;

    public AuthorizationDecision isAuthDoctor(Authentication auth, long patientId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        if(isAuthDoctor(user, patientId)) {
            return new AuthorizationDecision(true);
        }

        throw new NotFoundException("Doctor not found");
    }

    public AuthorizationDecision isAuthDoctorOrSelf(Authentication auth, long patientId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        if(user.getId().equals(patientId) || isAuthDoctor(user, patientId)) {
            return new AuthorizationDecision(true);
        }

        throw new NotFoundException("Doctor not found");
    }

    public AuthorizationDecision hasStudyAuth(Authentication auth, long studyId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        Study study = ss.getStudyById(studyId).orElseThrow(() -> new NotFoundException("Study not found"));

        if(user.getId().equals(study.getPatient().getId())) {//TODO:changed when migrating jpa, check later (mostly using directly .getId() without checking first)
            return new AuthorizationDecision(true);
        } else if(user.getRole().equals(UserRoleEnum.DOCTOR) && ass.hasAuthStudy(study.getId(), user.getId())) {
            return new AuthorizationDecision(true);
        }

        throw new NotFoundException("Study not found");
    }

    public AuthorizationDecision hasFileAuth(Authentication auth, long studyId, long fileId) {
        Boolean hasStudyAuth = hasStudyAuth(auth, studyId).isGranted();

        if(hasStudyAuth) {
            return new AuthorizationDecision(ss.isFileInStudy(studyId, fileId));
        }

        return new AuthorizationDecision(false);
    }

    private boolean isAuthDoctor(User user, long patientId) {
        return user.getRole().equals(UserRoleEnum.DOCTOR) && ads.hasAuthDoctor(patientId, user.getId());
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof PawAuthUserDetails details)) {
            return null;
        }

        return details.getUser();
    }
}
