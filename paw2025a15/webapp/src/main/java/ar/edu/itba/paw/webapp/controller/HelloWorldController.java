package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.models.DoctorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class HelloWorldController {
    
    @Autowired
    private DoctorDetailService dds;


    @RequestMapping("/")
    public ModelAndView helloWorld(@ModelAttribute("searchForm") final SearchForm searchForm) {
        final ModelAndView mav = new ModelAndView("index");
        List<DoctorView> doctors;
        if (searchForm.getQuery() != null && !searchForm.getQuery().isEmpty()) {
            doctors = dds.findDoctorsByName(searchForm.getQuery());
        } else {
            doctors = dds.getAllDoctors();
        }
        mav.addObject("docList", doctors );
        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("searchForm") final SearchForm searchForm) {
        ModelAndView mav = new ModelAndView("index");
        List<DoctorView> doctors = dds.findDoctorsByName(searchForm.getQuery());
        mav.addObject("docList", doctors);
        return mav;
    }

    @RequestMapping("/estudios")
    public ModelAndView estudios() {
        final ModelAndView mav = new ModelAndView("estudios");
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping("/obras-sociales")
    public ModelAndView obrasSociales() {
        final ModelAndView mav = new ModelAndView("obras-sociales");
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }
}