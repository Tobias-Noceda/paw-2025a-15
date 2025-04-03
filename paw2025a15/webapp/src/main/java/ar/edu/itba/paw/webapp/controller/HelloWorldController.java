package ar.edu.itba.paw.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.DoctorService;

@Controller
public class HelloWorldController {

    private final DoctorService ds;

    private final EmailService es;

    @Autowired
    public HelloWorldController(final DoctorService ds, final EmailService es) {
        this.ds = ds;
        this.es = es;
    }

    private List<SelectItem> getObrasSociales() {
        List<SelectItem> obrasSociales = List.of(
                new SelectItem("1","OSDE"),
                new SelectItem("2","Swiss Medical"),
                new SelectItem("3","Galeno")
        );

        return obrasSociales;
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
        mav.addObject("obrasSocialesItems", getObrasSociales());
        return mav;
    }

    @RequestMapping("/doctor-{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("doctor-detail");
        ds.findById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        ds.create(form.getName(), form.getEmail(), form.getObrasSociales(),form.getSpecialty(), form.getSchedules());
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("form", form);
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("register");
    }

    @RequestMapping(value = "/testMail")
    public ModelAndView testMail(){
        es.sendTestEmail();
        return new ModelAndView("redirect:/");
    }

    protected class SelectItem {
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