package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Controller
public class HelloWorldController {

    private final UserService us;

    private final EmailService es;

    @Autowired
    public HelloWorldController(final UserService us, final EmailService es){
        this.us = us;
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

    @RequestMapping("/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("profile");
        us.findById(id).ifPresent(user -> mav.addObject("user", user));
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "email", required = true) final String email, @RequestParam(value = "password", required = true) final String password){
        final User user = us.create(email, password);

        final var mav = new ModelAndView("index");
        mav.addObject("user", user);
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
        
}
