package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.RecoverForm;
import ar.edu.itba.paw.interfaces.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Controller
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private PatientCoverageService pcs;

    @Autowired
    private AppointmentService as;

    @Autowired
    private EmailService es;

    @RequestMapping("/patients/{id:\\d+}")
    public ModelAndView patientProfile(@PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("patientProfile");
        User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such patient"));
        mav.addObject("patient", patient);
        mav.addObject("patientInsurance", pcs.getInsuranceById(id));
        mav.addObject("patientAppointments", as.getAppointmentDataByPatientId(id));
        return mav;
    }

    @RequestMapping("/register/choose")
    public ModelAndView registerChoose(){
        ModelAndView mav = new ModelAndView("registerOne");
        return mav;
    }

    @RequestMapping("/forgot-password")
    public ModelAndView forgotPassword(){
        ModelAndView mav = new ModelAndView("forgotPassword");
        return mav;
    }

    @RequestMapping("/recover-password")
    public ModelAndView recoverPassword(
            @ModelAttribute("recoverPass")RecoverForm form
            ){
        ModelAndView mav = new ModelAndView("login");
        //TODO: desharcodear esto
        es.sendPasswordResetEmail(us.getUserByEmail(form.getEmail()).orElseThrow(()->new IllegalArgumentException("No such email")));
        return mav;
    }

    @RequestMapping("/logout")
    public ModelAndView logout(){
        return new ModelAndView("redirect:/");
    }
}
