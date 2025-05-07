package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.validation.Valid;

import ar.edu.itba.paw.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.CreateStudyForm;
import ar.edu.itba.paw.form.FileFilterForm;
import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;

@Controller
public class StudyController {

    @Autowired
    private StudyService ss;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Autowired
    private EmailService es;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(path = "/upload-study/{patientId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createStudyForm(
        @PathVariable("patientId") int patientId,
        @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm,
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("landingForm") final LandingForm landingForm
        ){
        User patient = us.getUserById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new NotFoundException("Patient not found");
        }

        ModelAndView mav = new ModelAndView("createStudy");
        mav.addObject("today", LocalDate.now() );
        mav.addObject("patient", patient);
        mav.addObject("user", us.getCurrentUser());
        mav.addObject("patientId", patientId);
        mav.addObject("studyTypeSelectItems", StudyTypeEnum.values());

        return mav;
    }

    @RequestMapping(path = "/upload-study/{patientId:\\d+}", method = RequestMethod.POST)
    public ModelAndView createStudy(
            @PathVariable("patientId") int patientId,
            @Valid @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm,
            @ModelAttribute("searchForm") final SearchForm searchForm,
            @ModelAttribute("landingForm") final LandingForm landingForm,
            BindingResult errors
    ) throws IOException{
        User patient = us.getUserById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new NotFoundException("Patient not found");
        }

        if (errors.hasErrors()) {
            return createStudyForm(patientId, createStudyForm,  searchForm, landingForm);
        }
        User user = us.getCurrentUser();

        LocalDateTime dateTime = LocalDateTime.now();

        File f = fs.create(createStudyForm.getFile().getBytes(), FileTypeEnum.fromString(createStudyForm.getFile().getContentType()));
        ss.create(createStudyForm.getType(), createStudyForm.getComment(), f.getId(), patientId, user.getId(), dateTime, createStudyForm.getDate());


        if(patientId != user.getId()) {
            es.sendRecievedStudyEmail(patient, user, f, createStudyForm.getComment(), dateTime);
            return new ModelAndView("redirect:/patient/" + patientId);
        } else {
            return new ModelAndView("redirect:/studies");
        }
    }

    @RequestMapping("/studies")
    public ModelAndView patientProfile(
        @ModelAttribute("filterForm") final FileFilterForm filterForm,
        Locale locale

    ) {
        ModelAndView mav = new ModelAndView("studies");

        User user = us.getCurrentUser();

        mav.addObject("user", user);
        mav.addObject("studyTypeSelectItems", SelectItem.getStudyTypeSelectItems(messageSource, locale));
        mav.addObject("patientAuthDoctors", dds.getAuthDoctorsByPatientId(user.getId()));
        mav.addObject("patientStudies", ss.getFilteredStudies(user.getId(), filterForm.getType(),filterForm.getMostRecent()));

        mav.addObject("landingForm", new LandingForm());
        
        return mav;
    }
}
