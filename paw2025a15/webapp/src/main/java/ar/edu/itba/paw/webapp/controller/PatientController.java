package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessLevelEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

@Controller
public class PatientController {

    @Autowired
    private UserService us;

    @Autowired
    private StudyService ss;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private PatientDetailService pds;

    @RequestMapping("/register/patient-form")
    public ModelAndView patient( @Valid @ModelAttribute("registerPatientForm") final PatientForm form) {
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
        User patient = us.getUserById(patientId)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found");            
        }

        final ModelAndView mav = new ModelAndView("patientDetail");
        
        User user = us.getCurrentUser();
        
        if(!dds.hasAuthDoctor(patientId, user.getId())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to view this patient");
        }
        
        mav.addObject("user", user);
        mav.addObject("patient", patient);
        mav.addObject("isAuthDoctor", dds.hasAuthDoctor(patientId, user.getId()));
        mav.addObject("allowedAccessLevels", dds.getAuthAccessLevelEnums(patientId, user.getId()).stream().map(AccessLevelEnum::name).toList());
        mav.addObject("searchForm", new SearchForm());
        mav.addObject("patientStudies", ss.getStudiesByPatientId(patientId));
        mav.addObject("patientDetails", pds.getDetailByPatientId(patientId).get());//TODO: conceptualmente no se puede hacer un get directo de un optional, hay q cambiarlo

        return mav;
    }
}