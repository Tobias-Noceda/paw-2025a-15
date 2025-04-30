package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.ChangePasswordForm;
import ar.edu.itba.paw.form.ProfileForm;
import ar.edu.itba.paw.form.RecoverForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.User;

import java.util.NoSuchElementException;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService us;

    @Autowired
    private PatientCoverageService pcs;

    @Autowired
    private DoctorCoverageService dcs;

    @Autowired
    private AppointmentService as;

    @Autowired
    private StudyService ss;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private EmailService es;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @RequestMapping("/patientProfile/{id:\\d+}")
    public ModelAndView patientProfile(@PathVariable("id") long id) {
        ModelAndView mav = new ModelAndView("patientProfile");
        User patient = us.getUserById(id).orElseThrow(() -> new IllegalArgumentException("No such patient"));
        mav.addObject("patient", patient);
        mav.addObject("patientInsurance", pcs.getInsuranceById(id));
        mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(id));
        mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(id));
        mav.addObject("patientStudies", ss.getStudiesByPatientId(id));
        mav.addObject("patientAuthDoctors", dds.getAuthDoctorsByPatientId(id));
        return mav;
    }

    @RequestMapping("/doctorProfile/{id:\\d+}")
    public ModelAndView doctorProfile(@PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("doctorProfile");
        User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such doctor"));
        mav.addObject("doctor", patient);
        mav.addObject("doctorInsurances", dcs.getInsurancesById(id));
        mav.addObject("doctorFutureAppointments", as.getFutureAppointmentDataByDoctorId(id));
        mav.addObject("doctorOldAppointments", as.getOldAppointmentDataByDoctorId(id));
        mav.addObject("doctorAuthPatients", us.getAuthPatientsPageByDoctorId(id, 1, 1000000));
        return mav;
    }

    @RequestMapping("/register/choose")
    public ModelAndView registerChoose() {
        ModelAndView mav = new ModelAndView("registerOne");
        return mav;
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword(@ModelAttribute("recoverPass") RecoverForm form) {
        ModelAndView mav = new ModelAndView("forgotPassword");
        return mav;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.GET)
    public ModelAndView showRecoverPasswordPage() {
        ModelAndView mav = new ModelAndView("forgotPassword");
        mav.addObject("successMessage", "checkEmail");
        return mav;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.POST)
    public ModelAndView recoverPassword(
            @Valid @ModelAttribute("recoverPass") RecoverForm form,
            BindingResult result
    ) {
        LOGGER.debug("Processing password recovery for email: {}", form.getEmail());
        ModelAndView mav;

        // Si hay errores de validación, volver a la página forgotPassword
        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found for email: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            return mav;
        }

        try {
            User user = us.getUserByEmail(form.getEmail()).orElseThrow(() -> new IllegalArgumentException("No such email"));
            es.sendPasswordResetEmail(user);
            LOGGER.info("Password reset email sent to: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("successMessage", "linkSent");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("No user found with email: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "emailNotFound");
            mav.addObject("recoverPass", form);
        } catch (Exception e) {
            LOGGER.error("Error sending password reset email: {}", e.getMessage());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "processingError");
            mav.addObject("recoverPass", form);
        }

        return mav;
    }
    
    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(value = "/changePassword/{token}/{id}", method = RequestMethod.GET)
    public ModelAndView changePassword(
        @PathVariable("id") long id, @PathVariable("token") String token ,
        @ModelAttribute("passwordForm") ChangePasswordForm form
    ){
        ModelAndView mav = new ModelAndView("changePassword");

        return mav;
    }


    @RequestMapping(value = "/changePassword/{token}/{id}", method = RequestMethod.POST)
    public ModelAndView changePassword(
        @ModelAttribute("passwordForm") final ChangePasswordForm form,
        @PathVariable("id") long id, @PathVariable("token") String token
    ){
        if(!form.getPassword().equals(form.getRepeatPassword())){
            //volver a pedirle al usuario
            ModelAndView mav = new ModelAndView("changePassword");
            mav.addObject("errorMessage", "Las contraseñas no coinciden");
            return mav;
        }
        ModelAndView mav = new ModelAndView("login");
        us.changePasswordByID(id, passwordEncoder.encode(form.getPassword()));
        return mav;
    }

    @RequestMapping("/profile")
    public ModelAndView profile(
        @AuthenticationPrincipal UserDetails userDetails,
        SearchForm form,
        @ModelAttribute("profileForm") ProfileForm profileForm
    ) {
        ModelAndView mav = new ModelAndView("profileInfo");

        User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));
        mav.addObject("user", user);
        mav.addObject("patientDetails", pds.getDetailByPatientId(user.getId()).get());//TODO: conceptualmente no se puede hacer un get directo de un optional, hay q cambiarlo
        mav.addObject("bloodTypes", BloodTypeEnum.values());
        return mav;
    }

}