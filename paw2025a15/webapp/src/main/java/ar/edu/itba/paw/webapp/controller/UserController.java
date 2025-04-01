package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.UserService;

@Controller
public class UserController {

    private final UserService us;

    @Autowired
    public UserController(final UserService us){
        this.us = us;
    }

    @RequestMapping(value = "/register", method=RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "email", required = true) final String email, @RequestParam(value = "password", required = true) final String password, @RequestParam(value = "name", required = true) final String name){
        final var mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("register");
    }
}
