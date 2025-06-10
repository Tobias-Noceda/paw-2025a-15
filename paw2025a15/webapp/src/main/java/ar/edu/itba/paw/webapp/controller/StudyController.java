package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.controller.util.SelectItem;
import ar.edu.itba.paw.webapp.form.CreateStudyForm;
import ar.edu.itba.paw.webapp.form.FileFilterForm;
import ar.edu.itba.paw.webapp.form.LandingForm;

@Controller
public class StudyController {

    @Autowired
    private StudyService ss;

    @Autowired
    private PatientService ps;

    @Autowired
    private AuthStudiesService ass;

    @Autowired
    private FileService fs;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(path = "/upload-study/{patientId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createStudyForm(
        @PathVariable("patientId") int patientId,
        @ModelAttribute("landingForm") final LandingForm landingForm,
        @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm,
        BindingResult errors,
        Locale locale,
        @ModelAttribute("user_data") User user
    ){
        Patient patient = ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new NotFoundException("Patient not found");
        }

        ModelAndView mav = new ModelAndView("createStudy");
        mav.addObject("today", LocalDate.now() );
        mav.addObject("patient", patient);
        mav.addObject("studyTypeSelectItems", SelectItem.getStudyTypeSelectItems(messageSource, locale));

        return mav;
    }

    @RequestMapping(path = "/upload-study/{patientId:\\d+}", method = RequestMethod.POST)
    public ModelAndView createStudy(
            @ModelAttribute("user_data") User user,
            @PathVariable("patientId") int patientId,
            @ModelAttribute("landingForm") final LandingForm landingForm,
            @Valid @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm,
            BindingResult errors,
            Locale locale
    ) throws IOException {
        if (errors.hasErrors()) {
            return createStudyForm(patientId, landingForm, createStudyForm, errors, locale, user);
        }
        String comment = "";
        if(createStudyForm.getComment() != null) {
            comment = createStudyForm.getComment();
        }
        File file = fs.create(createStudyForm.getFile().getBytes(), FileTypeEnum.fromString(createStudyForm.getFile().getContentType()));
        Study study = ss.create(createStudyForm.getType(), comment, List.of(file), patientId, user.getId(), createStudyForm.getDate());
        ass.authStudyForDoctorIdList(createStudyForm.getAuthDoctorIds(), study.getId());
        if(patientId != user.getId()) {
            return new ModelAndView("redirect:/patient/" + patientId);
        } else {
            return new ModelAndView("redirect:/studies");
        }
    }

    @RequestMapping("/studies")
    public ModelAndView patientProfile(
        @ModelAttribute("user_data") User user,
        @ModelAttribute("filterForm") final FileFilterForm filterForm,
        Locale locale

    ) {
        ModelAndView mav = new ModelAndView("studies");

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        mav.addObject("studyTypeSelectItems", SelectItem.getStudyTypeSelectItems(messageSource, locale));
        mav.addObject("patient", (Patient) user);
        mav.addObject("patientStudies", ss.getFilteredStudies(user.getId(), filterForm.getType(), filterForm.getMostRecent()));

        mav.addObject("landingForm", new LandingForm());
        
        return mav;
    }

    @RequestMapping("/study-info/{studyId:\\d+}")
    public ModelAndView studyInfo(
            @ModelAttribute("user_data") User user,
            @PathVariable("studyId") int studyId,
            Locale locale
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        ModelAndView mav = new ModelAndView("studyInfo");
        Patient patient = (Patient) user;

        // Obtener el estudio
        Study study = ss.getStudyById(studyId).orElseThrow();

        // Obtener todos los doctores asociados al paciente (autorizados o no)
        List<Doctor> doctors = patient.getAuthorizedDoctors();

        List<Doctor> studyAuthDoctors = study.getAuthDoctors();

        mav.addObject("landingForm", new LandingForm());
        mav.addObject("study", study);
        mav.addObject("authDoctors", doctors);
        mav.addObject("studyAuthDoctors", studyAuthDoctors);
        return mav;
    }

    @RequestMapping(path = "/delete-study/{studyId:\\d+}", method = RequestMethod.POST)
    public ModelAndView deleteStudy(
            @PathVariable("studyId") int studyId,
            Locale locale
    ){
        ss.deleteStudy(studyId);
        return new ModelAndView("redirect:/studies");
    }
}
