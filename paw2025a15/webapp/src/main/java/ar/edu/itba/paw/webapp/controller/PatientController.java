package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Locale;

@Controller
public class PatientController {

    @Autowired
    private UserService us;

    @RequestMapping("/patient-form")
    public ModelAndView medico(@ModelAttribute("registerPatientForm") final PatientForm form, Locale locale) {
        final ModelAndView mav = new ModelAndView("patientForm");
        mav.addObject("patient", form);
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }


    @RequestMapping(value = "/createPatient", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerMedicForm") final PatientForm form,
            final BindingResult errors,
            @ModelAttribute("filterForm") final FilterForm filterForm
    ){
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("patientForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
        us.create(form.getEmail(), form.getPassword(), form.getName() + " " + form.getSurname());
        ModelAndView mav = new ModelAndView("redirect:/");
        return mav;
    }
}
