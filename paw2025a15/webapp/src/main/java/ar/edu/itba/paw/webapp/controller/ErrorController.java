package ar.edu.itba.paw.webapp.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Controller
public class ErrorController {
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handle401Exception() {
        ModelAndView mav = new ModelAndView("errorPages/403");
        return mav;
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handle404Exception() {
        ModelAndView mav = new ModelAndView("errorPages/404");
        return mav;
    }
    
    @RequestMapping("/{code:[0-9]{3}}")
    public ModelAndView error(@PathVariable("code") String code) {
        return new ModelAndView("errorPages/" + code);
    }
}
