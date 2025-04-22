package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.UserRoleEnum;
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
    ) {
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("patientForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        // Verificar si el email ya existe
        if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists", "El email ya está registrado");
            ModelAndView mav = new ModelAndView("patientForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        try {
            us.create(
                    form.getEmail(),
                    passwordEncoder.encode(form.getPassword()),
                    form.getName() + " " + form.getSurname(),
                    form.getPhoneNumber(),
                    UserRoleEnum.PATIENT
            );
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            errors.reject("error.generic", "Ocurrió un error al registrar el paciente. Por favor, intenta nuevamente.");
            ModelAndView mav = new ModelAndView("patientForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
    }
}