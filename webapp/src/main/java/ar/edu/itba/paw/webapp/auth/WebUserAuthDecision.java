package ar.edu.itba.paw.webapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Controller;

import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.AppointmentNewId;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Controller
public class WebUserAuthDecision {

    @Autowired
    private UserService us;

    @Autowired
    private DoctorShiftService dss;

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

        if(user.getId().equals(study.getPatient().getId())) {
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

    public AuthorizationDecision canAccessAppointments(Authentication auth, RequestAuthorizationContext context) {
        // If not authenticated, let Spring Security handle it (will trigger 401)
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        // From here on, user is authenticated, so any denial is a 403 (Forbidden)
        String statusStr = context.getRequest().getParameter("status");
        String userIdStr = context.getRequest().getParameter("userId");

        if (statusStr == null || userIdStr == null) {
            return new AuthorizationDecision(false);
        }

        try {
            AppointmentStatusEnum status = AppointmentStatusEnum.fromString(statusStr);
            Long userId = Long.valueOf(userIdStr);

            if (status.equals(AppointmentStatusEnum.FREE) && user.getRole().equals(UserRoleEnum.PATIENT)) {
                return new AuthorizationDecision(true);
            } else if (status.equals(AppointmentStatusEnum.COMPLETED) && user.getRole().equals(UserRoleEnum.PATIENT)) {
                return new AuthorizationDecision(true);  // Only patients can see their completed appointments
            } else if (user.getRole().equals(UserRoleEnum.ADMIN)) {
                return new AuthorizationDecision(true);  // Admin can see everything
            } else {
                return new AuthorizationDecision(user.getId().equals(userId));  // 403 if not their appointments
            }
        } catch (IllegalArgumentException e) {
            return new AuthorizationDecision(false);  // 403: Invalid parameters
        }
    }

    public AuthorizationDecision canModifyAppointment(Authentication auth, String appointmentId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        // Patients can modify their own appointments
        if (user.getRole().equals(UserRoleEnum.PATIENT)) {
            return new AuthorizationDecision(true); // Further checks can be added in the service layer
        }

        AppointmentNewId id = AppointmentNewId.fromId(appointmentId);

        DoctorSingleShift shift = dss.getShiftById(id.getShiftId()).orElseThrow(() -> new NotFoundException("Shift with shiftId: " + id.getShiftId() + " does not exist!"));


        return new AuthorizationDecision(user.getRole().equals(UserRoleEnum.DOCTOR) && shift.getDoctor().getId().equals(user.getId()));
    }

    public AuthorizationDecision isDoctor(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(user.getRole().equals(UserRoleEnum.DOCTOR));
    }

    public AuthorizationDecision isPatient(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(user.getRole().equals(UserRoleEnum.PATIENT));
    }

    public AuthorizationDecision isAdmin(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(user.getRole().equals(UserRoleEnum.ADMIN));
    }

    public AuthorizationDecision isSelf(Authentication auth, long userId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(user.getId().equals(userId));
    }

    private boolean isAuthDoctor(User user, long patientId) {
        return user.getRole().equals(UserRoleEnum.DOCTOR) && ads.hasAuthDoctor(patientId, user.getId());
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof String email) {
            return us.getUserByEmail(email).orElse(null);
        }

        return null;
    }
}
