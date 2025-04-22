package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(path = "/supersecret/upload/{patientId:\\d+}/{doctorId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createStudyForm(@PathVariable("patientId") int patientId, @PathVariable("doctorId") int doctorId, @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm){
        ModelAndView mav = new ModelAndView("createStudy");
        User patient = us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("Invalid patient ID: " + patientId));
        mav.addObject("patientName", patient.getName());
        mav.addObject("patientId", patientId);
        mav.addObject("doctorId", doctorId);
        mav.addObject("studyTypeSelectItems", StudyTypeEnum.values());//TODO revisar esto con el jsp
        return mav;
    }

    @RequestMapping(path = "/supersecret/upload/{patientId:\\d+}/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView createStudy(@PathVariable("patientId") int patientId, @PathVariable("doctorId") int doctorId, @Valid @ModelAttribute("createStudyForm") CreateStudyForm createStudyForm, BindingResult errors) throws IOException{
        if (errors.hasErrors()) {
            return createStudyForm(patientId, doctorId, createStudyForm);
        }
        //TODO esta verificacion tendria que estar en el form directamente
        //if (fileType == null || !(fileType.equals("image/png") || fileType.equals("image/jpeg") || fileType.equals("application/pdf"))) {
        //throw new IllegalArgumentException("Unsupported file type: " + fileType);
        // }
        User patient = us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("Invalid patient ID: " + patientId));
        User doctor = us.getUserById(doctorId).orElseThrow(() -> new IllegalArgumentException("Invalid doctor ID: " + doctorId));
        LocalDateTime dateTime = LocalDateTime.now();
        
        File f = fs.create(createStudyForm.getFile().getBytes(), FileTypeEnum.fromString(createStudyForm.getFile().getContentType()));
        ss.create(createStudyForm.getType(), createStudyForm.getComment(), f.getId(), patientId, doctorId, dateTime, createStudyForm.getDate());


        es.sendRecievedStudyEmail(patient, doctor, f, createStudyForm.getComment(), dateTime);

        return new ModelAndView("redirect:/");
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
