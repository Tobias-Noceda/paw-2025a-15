package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;

@Controller
public class PatientController {

    @Autowired
    private UserService us;

    @Autowired
    private StudyService ss;

    @Autowired
    private DoctorDetailService dds;

    @RequestMapping("/register/patient-form")
    public ModelAndView patient(@ModelAttribute("registerPatientForm") final PatientForm form) {
        final ModelAndView mav = new ModelAndView("patientForm");
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping("/patient/{patientId:\\d+}")
    public ModelAndView patient(
            @PathVariable("patientId") int patientId,
            @ModelAttribute("registerPatientForm") final PatientForm form,
            @ModelAttribute("filterForm") final FilterForm filterForm
    ) {
        final ModelAndView mav = new ModelAndView("patientDetail");
        
        final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        // Si llegó hasta acá, está logueado
        final User user = us.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if(!dds.hasAuthDoctor(patientId, user.getId())) {
            return new ModelAndView("redirect:/");
        }
        
        mav.addObject("user", user);
        
        mav.addObject("patient", us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("Invalid patient ID: " + patientId)));
        mav.addObject("searchForm", new SearchForm());
        mav.addObject("patientStudies", ss.getStudiesByPatientId(patientId));

        return mav;
    }
}