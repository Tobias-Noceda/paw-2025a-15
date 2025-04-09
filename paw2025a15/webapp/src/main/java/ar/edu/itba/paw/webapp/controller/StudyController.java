package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.createStudyForm;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

@Controller
public class StudyController {

    @Autowired
    private StudyService ss;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @RequestMapping(path = "/supersecret/upload/{patientId:\\d+}/{doctorId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createStudyForm(@PathVariable("patientId") int patientId, @PathVariable("doctorId") int doctorId, @ModelAttribute("createStudyForm") createStudyForm createStudyForm){
        ModelAndView mav = new ModelAndView("createStudy");
        User patient = us.getUserById(patientId).orElseThrow(() -> new IllegalArgumentException("Invalid patient ID: " + patientId));
        mav.addObject("patientName", patient.getName());
        mav.addObject("patientId", patientId);
        mav.addObject("doctorId", doctorId);
        return mav;
    }

    @RequestMapping(path = "/supersecret/upload/{patientId:\\d+}/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView createStudy(@PathVariable("patientId") int patientId, @PathVariable("doctorId") int doctorId, @ModelAttribute("createStudyForm") createStudyForm createStudyForm, BindingResult errors) throws IOException{
        if (errors.hasErrors()) {
            return createStudyForm(patientId, doctorId, createStudyForm);
        }
        //TODO esta verificacion tendria que e star en el form directamente
        //if (fileType == null || !(fileType.equals("image/png") || fileType.equals("image/jpeg") || fileType.equals("application/pdf"))) {
//throw new IllegalArgumentException("Unsupported file type: " + fileType);
       // }
        File f = fs.create(createStudyForm.getFile().getBytes(), createStudyForm.getFile().getContentType());
        ss.create(createStudyForm.getType(), f.getId(), patientId, doctorId, LocalDateTime.now());
        return new ModelAndView("redirect:/");
    }
}
