package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Controller
public class HelloWorldController {

    private final UserService us;

    @Autowired
    public HelloWorldController(final UserService us){
        this.us = us;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("user", us.createUser("pawpaw@itba.edu.ar", "password"));
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "email", required = true) final String email, @RequestParam(value = "password", required = true) final String password){
        final User user = us.createUser(email, password);

        final var mav = new ModelAndView("index");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("register");
    }
}
