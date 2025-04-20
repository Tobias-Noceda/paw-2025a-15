package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.SearchForm;


@Controller
public class GeneralController {

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");

    }

    @RequestMapping("/studies")
    public ModelAndView studies() {
        final ModelAndView mav = new ModelAndView("studies");
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping("/insurances")
    public ModelAndView insurances() {
        final ModelAndView mav = new ModelAndView("insurances");
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }
}