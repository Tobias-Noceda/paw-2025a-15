package ar.edu.itba.paw.webapp.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Controller;

import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.AppointmentNewId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Controller
public class WebUserAuthDecision {

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

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

        return new AuthorizationDecision(false);
    }

    public AuthorizationDecision isAuthDoctorOrSelf(Authentication auth, Long patientId) {
        if (patientId!=null && ps.getPatientById(patientId).isEmpty()) return new AuthorizationDecision(true);

        User user = getAuthenticatedUser(auth);
        if (user == null || patientId == null) {
            return new AuthorizationDecision(false);
        }

        if(isSelf(auth, patientId) || isAuthDoctor(user, patientId)) {
            return new AuthorizationDecision(true);
        }

        return new AuthorizationDecision(false);
    }

    public AuthorizationDecision isAuthDoctorByParamOrSelf(Authentication auth, Long patientId, Long doctorId) {
        User user = getAuthenticatedUser(auth);
        if (user == null || patientId == null) {
            return new AuthorizationDecision(false);
        }

        if(isSelf(auth, patientId)) {
            return new AuthorizationDecision(true);
        }

        if (doctorId==null) return new AuthorizationDecision(true);

        if (isAuthDoctor(user, patientId) && user.getId().equals(doctorId)) {
            return new AuthorizationDecision(true);
        }

        return new AuthorizationDecision(false);
    }

    public AuthorizationDecision isDoctorByParam(Authentication auth, Long doctorId) {
        if (doctorId==null) return new AuthorizationDecision(true);

        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        Doctor doctor = ds.getDoctorById(user.getId()).orElse(null);
        if(doctor == null) return new AuthorizationDecision(false);

        if(user.getId().equals(doctorId)) {
            return new AuthorizationDecision(true);
        }

        return new AuthorizationDecision(false);
    }

    public AuthorizationDecision hasStudyAuth(Authentication auth, Long patientId, Long studyId) {
        if(patientId!=null && ps.getPatientById(patientId).isEmpty()) return new AuthorizationDecision(true);

        User user = getAuthenticatedUser(auth);
        System.out.println("User in hasStudyAuth: " + (user != null ? user.getId() : "null"));
        if (user == null || studyId == null) {
            return new AuthorizationDecision(false);
        }

        Study study = ss.getStudyById(studyId).orElse(null);

        if (study == null) return new AuthorizationDecision(true);

        if(isStudyAuth(user, study)) return new AuthorizationDecision(true);

        return new AuthorizationDecision(false);
    }

    public AuthorizationDecision canDeleteStudy(Authentication auth, Long studyId) {
        User user = getAuthenticatedUser(auth);
        if (user == null || studyId == null) {
            return new AuthorizationDecision(false);
        }

        Study study = ss.getStudyById(studyId).orElse(null);

        if (study == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(isSelf(auth, study.getPatient().getId()));
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

        DoctorSingleShift shift = dss.getShiftById(id.getShiftId()).orElse(null);

        if(shift == null){
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(user.getRole().equals(UserRoleEnum.DOCTOR) && shift.getDoctor().getId().equals(user.getId()));
    }

    public AuthorizationDecision canModifyDoctorShifts(Authentication auth, String doctorIdStr) {
        return new AuthorizationDecision(isDoctor(auth) && isSelf(auth, Long.parseLong(doctorIdStr)));
    }

    public AuthorizationDecision canSeePatientInfo(Authentication auth, long patientId, AccessLevelEnum accessLevel) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return new AuthorizationDecision(false);
        }

        if (isSelf(auth, patientId)) {
            return new AuthorizationDecision(true);
        }

        if (!isAuthDoctor(user, patientId)){
            return new AuthorizationDecision(false);
        }

        List<AccessLevelEnum> doctorAccessLevel = ads.getAuthAccessLevelEnums(patientId, user.getId());

        return new AuthorizationDecision(doctorAccessLevel.contains(accessLevel));
    }

    public boolean isDoctor(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return false;
        }

        return user.getRole().equals(UserRoleEnum.DOCTOR);
    }

    public AuthorizationDecision isSelfDecision(Authentication auth, long userId) {
        return new AuthorizationDecision(isSelf(auth, userId));
    }

    private boolean isPatient(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return false;
        }

        return user.getRole().equals(UserRoleEnum.PATIENT);
    }

    private boolean isAdmin(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return false;
        }

        return user.getRole().equals(UserRoleEnum.ADMIN);
    }

    private boolean isSelf(Authentication auth, long userId) {
        User user = getAuthenticatedUser(auth);
        if (user == null) {
            return false;
        }

        return user.getId().equals(userId);
    }

    private boolean isAuthDoctor(User user, long patientId) {
        return user.getRole().equals(UserRoleEnum.DOCTOR) && ads.hasAuthDoctor(patientId, user.getId());
    }

    private boolean isStudyAuth(User user, Study study){
        if(user.getId().equals(study.getPatient().getId())) {
            return true;
        } else if(user.getRole().equals(UserRoleEnum.DOCTOR) && ass.hasAuthStudy(study.getId(), user.getId())) {
            return true;
        }
        return false;
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        return null;
    }
}
