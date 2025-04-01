package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EmailService;

@Controller
public class HelloWorldController {

    private final EmailService es;

    @Autowired
    public HelloWorldController(final EmailService es){
        this.es = es;
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
    public ModelAndView medico() {
        final ModelAndView mav = new ModelAndView("medico");
        return mav;
    }

    @RequestMapping(value = "/testMail")
    public ModelAndView testMail(){
        es.sendTestEmail();
        return new ModelAndView("redirect:/");
    }
        
}
