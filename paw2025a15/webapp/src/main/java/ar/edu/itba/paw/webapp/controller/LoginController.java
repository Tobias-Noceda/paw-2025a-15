package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView mav = new ModelAndView("login");
        if (error != null) {
            switch (error) {
                case "userNotFound" -> mav.addObject("errorMessage", "error.invalidCredentials");
                case "invalidCredentials" -> mav.addObject("errorMessage", "error.invalidCredentials");
                case "credentialsNotFound" -> mav.addObject("errorMessage", "error.credentialsNotFound");
                default -> mav.addObject("errorMessage", "error.invalidCredentials");
            }
        }
        return mav;
    }
}