package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    @RequestMapping("/{code:[0-9]{3}}")
    public ModelAndView error(@PathVariable("code") String code) {
        return new ModelAndView("errorPages/" + code);
    }
}
