package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.LabForm;
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
import java.util.Locale;

@Controller
public class LabController {

    @Autowired
    private UserService us;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/register/lab-form")
    public ModelAndView medico(@ModelAttribute("registerLabForm") final LabForm form, Locale locale) {
        final ModelAndView mav = new ModelAndView("labForm");
        mav.addObject("lab", form);
        mav.addObject("searchForm", new SearchForm());
        //System.out.println("Redirigiendo a labForm..."); TODO should be a log
        return mav;
    }

    @RequestMapping(value = "/createLab", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerLabForm") final LabForm form,
            final BindingResult errors,
            Locale locale,
            @ModelAttribute("filterForm") final FilterForm filterForm
    ) {

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.passwordMismatch");
            ModelAndView mav = new ModelAndView("labForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
        // Verificar si el email ya existe
        if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists");
            ModelAndView mav = new ModelAndView("labForm");
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
        //System.out.println("new lab:" + form.getName());TODO should be a log
        //us.create(form.getEmail(), passwordEncoder.encode(form.getPassword()), form.getName(), form.getPhoneNumber(), UserRoleEnum.LABORATORY, 1);
        return new ModelAndView("redirect:/");
    }
}
