package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.DoctorForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloWorldController {

    private final UserService us;

    @Autowired
    public HelloWorldController(final UserService us) {
        this.us = us;
    }

    private List<SelectItem> getObrasSociales() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("OS1", "Obra Social 1"));
        items.add(new SelectItem("OS2", "Obra Social 2"));
        items.add(new SelectItem("OS3", "Obra Social 3"));
        // Añadir más obras sociales aquí

        return items;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/estudios")
    public ModelAndView estudios() {
        final ModelAndView mav = new ModelAndView("estudios");
        return mav;
    }

    @RequestMapping("/obras-sociales")
    public ModelAndView obrasSociales() {
        final ModelAndView mav = new ModelAndView("obras-sociales");
        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("items", getObrasSociales());
        return mav;
    }

    @RequestMapping("/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("profile");
        us.findById(id).ifPresent(user -> mav.addObject("user", user));
        return mav;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "email", required = true) final String email, @RequestParam(value = "password", required = true) final String password) {
        final User user = us.create(email, password);

        final var mav = new ModelAndView("index");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        //us.createMedic(form.getName(), form.getEmail());
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("form", form);
        return mav;
    }


    public class SelectItem {
        private String value;
        private String label;

        // Constructor, getters y setters
        public SelectItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}