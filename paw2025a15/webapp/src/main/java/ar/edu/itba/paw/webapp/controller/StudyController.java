package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.CreateStudyForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.FileTypeEnum;
import ar.edu.itba.paw.models.StudyTypeEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;

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

    @RequestMapping(path = "/upload-file/{patientId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createStudyForm(
        @PathVariable("patientId") int patientId,
        @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm
    ){
        User patient = us.getUserById(patientId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found");            
        }

        if(patient.getRole() != UserRoleEnum.PATIENT) throw new IllegalArgumentException("Invalid patient ID: " + patientId);

        User user = us.getCurrentUser()
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found"));

        if(user.getRole() != UserRoleEnum.DOCTOR && user.getRole() != UserRoleEnum.PATIENT) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to upload study for this patient");
        }
        
        if(user.getId() != patientId || !dds.hasAuthDoctor(patientId, user.getId())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to upload study for this patient");
        }
        
        ModelAndView mav = new ModelAndView("createStudy");
        mav.addObject("patient", patient);
        mav.addObject("patientId", patientId);
        mav.addObject("studyTypeSelectItems", StudyTypeEnum.values());

        return mav;
    }

    @RequestMapping(path = "/upload-file/{patientId:\\d+}", method = RequestMethod.POST)
    public ModelAndView createStudy(
        @PathVariable("patientId") int patientId,
        @Valid @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm,
        BindingResult errors
    ) throws IOException{
        User patient = us.getUserById(patientId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found"));

        if(!patient.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patient not found");            
        }

        if(patient.getRole() != UserRoleEnum.PATIENT) throw new IllegalArgumentException("Invalid patient ID: " + patientId);

        User user = us.getCurrentUser()
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found"));

        if(user.getRole() != UserRoleEnum.DOCTOR && user.getRole() != UserRoleEnum.PATIENT) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to upload study for this patient");
        }
        
        

        if (errors.hasErrors()) {
            return createStudyForm(patientId, createStudyForm);
        }

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
        @ModelAttribute("searchForm") SearchForm searchForm
    ) {
        try {
            ModelAndView mav = new ModelAndView("studies");
        
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            mav.addObject("user", user);
            mav.addObject("patientStudies", ss.getStudiesByPatientId(user.getId()));
            mav.addObject("patientAuthDoctors", dds.getAuthDoctorsByPatientId(user.getId()));
            
            return mav;
        } catch (Exception e) {
        }

        return new ModelAndView("redirect:/");
    }
}
