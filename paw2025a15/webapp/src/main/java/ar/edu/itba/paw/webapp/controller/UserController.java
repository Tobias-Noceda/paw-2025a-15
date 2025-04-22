package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.ChangePasswordForm;
import ar.edu.itba.paw.form.RecoverForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

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
    private EmailService es;

    @Autowired
    private PasswordEncoder passwordEncoder;


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
        mav.addObject("doctorAuthPatients", us.getAuthPatientsByDoctorId(id));
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
        ModelAndView mav = new ModelAndView("recoverPassword");
        mav.addObject("successMessage", "Revisa tu correo para continuar con el proceso de recuperación.");
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
            mav = new ModelAndView("recoverPassword");
            mav.addObject("successMessage", "Se ha enviado un enlace de recuperación a tu correo.");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("No user found with email: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "El correo no está registrado.");
            mav.addObject("recoverPass", form);
        } catch (Exception e) {
            LOGGER.error("Error sending password reset email: {}", e.getMessage());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "Hubo un error al procesar tu solicitud. Intenta de nuevo más tarde.");
            mav.addObject("recoverPass", form);
        }

        return mav;
    }
    
    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:/");
    }


    @RequestMapping(value = "/changePassword/{token}/{id}", method = RequestMethod.GET)
    public ModelAndView changePassword(@PathVariable("id") long id, @PathVariable("token") String token ,
                                       @ModelAttribute("passwordForm") ChangePasswordForm form){
        ModelAndView mav = new ModelAndView("changePassword");

        return mav;
    }


    @RequestMapping(value = "/changePassword/{token}/{id}", method = RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute("passwordForm") final ChangePasswordForm form,
                                       @PathVariable("id") long id, @PathVariable("token") String token){
        System.out.println(form);
        System.out.println("id = " + id);
        System.out.println(token);
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
    public ModelAndView profile(@AuthenticationPrincipal UserDetails userDetails, SearchForm form) {
        ModelAndView mav = new ModelAndView("profileInfo");
        System.out.println(userDetails.getUsername());
        User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("No such email"));
        mav.addObject("user", user);

        return mav;
    }
/*
    @RequestMapping("/updateProfile/{id}/{picId}")//TODO esto es a modo de ejemplo, no va en la webapp
    public ModelAndView updateProfile(@PathVariable("id") long id, @PathVariable("picId") long picId){
        User user = us.getUserById(id).orElseThrow(() -> new IllegalArgumentException("No such user_id"));
        us.editUser(id, user.getName(), user.getTelephone(), picId);
        return new ModelAndView("redirect:/");
    }
*/

}