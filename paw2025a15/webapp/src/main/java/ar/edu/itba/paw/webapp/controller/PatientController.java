package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.controller.Util.SelectItem;
import ar.edu.itba.paw.webapp.form.FileFilterForm;
import ar.edu.itba.paw.webapp.form.LandingForm;
import ar.edu.itba.paw.webapp.form.PatientForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

import java.util.Locale;

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

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/patient/{patientId:\\d+}")
    public ModelAndView patient(
            @ModelAttribute("user_data") User user,
            @PathVariable("patientId") int patientId,
            @ModelAttribute("registerPatientForm") final PatientForm form,
            @ModelAttribute("filterForm") final FileFilterForm filterForm,
            Locale locale
    ) {
        User patient = us.getUserById(patientId)
            .orElseThrow(() -> new NotFoundException("Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new NotFoundException("Patient not found");            
        }

        final ModelAndView mav = new ModelAndView("patientDetail");
        
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        mav.addObject("patientDetails", pds.getDetailByPatientId(patientId).orElseThrow(() -> new NotFoundException("Patient details not found for user with id: " + patientId)));
        mav.addObject("patient", patient);
        mav.addObject("isAuthDoctor", ads.hasAuthDoctor(patientId, user.getId()));
        mav.addObject("allowedAccessLevels", ads.getAuthAccessLevelEnums(patientId, user.getId()).stream().map(AccessLevelEnum::name).toList());
        mav.addObject("landingForm", new LandingForm());
        mav.addObject("patientStudies", ss.getFilteredStudiesByPatientIdAndDoctorId(patientId, user.getId(),filterForm.getType(),filterForm.getMostRecent()));
        mav.addObject("studyTypeSelectItems", SelectItem.getStudyTypeSelectItems(messageSource, locale));
        return mav;
    }
}