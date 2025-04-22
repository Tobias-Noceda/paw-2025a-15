package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/register/patient-form")
    public ModelAndView medico(
        @ModelAttribute("registerPatientForm") final PatientForm form
    ) {
        final ModelAndView mav = new ModelAndView("patientForm");
        mav.addObject("patient", form);
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }


    @RequestMapping(value = "/createPatient", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerPatientForm") final PatientForm form,
            final BindingResult errors,
            @ModelAttribute("filterForm") final FilterForm filterForm
    ){
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("patientForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
        // TODO: Verificar que el email no exista en la base de datos
        System.out.println("Contraseña " + passwordEncoder.encode(form.getPassword()));
        us.create(form.getEmail(), passwordEncoder.encode(form.getPassword()), form.getName() + " " + form.getSurname(), "11 1234-5678"); //TODO: Agregar telefono al form
        ModelAndView mav = new ModelAndView("redirect:/");
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
