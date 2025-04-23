package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.LabForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LabController {

    @RequestMapping("/register/lab-form")
    public ModelAndView medico(@ModelAttribute("registerLabForm") final LabForm form) {
        final ModelAndView mav = new ModelAndView("labForm");
        mav.addObject("lab", form);

        return mav;
    }
}
