package ar.edu.itba.paw.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {
    @RequestMapping("/400")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView badRequest() {
        return new ModelAndView("errorPages/400");
    }

    @RequestMapping("/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView forbidden() {
        return new ModelAndView("errorPages/403");
    }

    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound() {
        return new ModelAndView("errorPages/404");
    }

    @RequestMapping("/405")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ModelAndView notAllowed() {
        return new ModelAndView("errorPages/405");
    }

    @RequestMapping("/408")
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ModelAndView timeout() {
        return new ModelAndView("errorPages/408");
    }
}
