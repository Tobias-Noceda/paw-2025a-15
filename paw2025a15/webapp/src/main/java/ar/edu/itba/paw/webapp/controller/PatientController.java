package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Controller
public class PatientController {

    @Autowired
    private UserService us;

    @Autowired
    private StudyService ss;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private AuthDoctorService ads;

    @RequestMapping("/patient/{patientId:\\d+}")
    public ModelAndView patient(
            @PathVariable("patientId") int patientId,
            @ModelAttribute("registerPatientForm") final PatientForm form
    ) {
        User patient = us.getUserById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new NotFoundException("Patient not found");            
        }

        final ModelAndView mav = new ModelAndView("patientDetail");
        
        User user = us.getCurrentUser();
        
        mav.addObject("user", user);
        mav.addObject("patient", patient);
        mav.addObject("isAuthDoctor", ads.hasAuthDoctor(patientId, user.getId()));
        mav.addObject("allowedAccessLevels", ads.getAuthAccessLevelEnums(patientId, user.getId()).stream().map(AccessLevelEnum::name).toList());
        mav.addObject("landingForm", new LandingForm());
        mav.addObject("patientStudies", ss.getStudiesByPatientIdAndDoctorId(patientId,user.getId()));
        mav.addObject("patientDetails", pds.getDetailByPatientId(patientId).get());//TODO: conceptualmente no se puede hacer un get directo de un optional, hay q cambiarlo

        return mav;
    }
}