package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class PatientController {

    @Autowired
    private UserService us;

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
        //System.out.println("Contraseña " + passwordEncoder.encode(form.getPassword()));
        us.create(form.getEmail(), passwordEncoder.encode(form.getPassword()), form.getName() + " " + form.getSurname(), form.getPhoneNumber()); //TODO: Agregar telefono al form
        ModelAndView mav = new ModelAndView("redirect:/");
        return mav;
    }
}
