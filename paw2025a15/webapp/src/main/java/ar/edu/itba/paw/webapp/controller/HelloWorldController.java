package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class HelloWorldController {
    @Autowired
    private DoctorDetailService dds;
    @Autowired
    private InsuranceService is;

    @RequestMapping("/")
    public ModelAndView helloWorld(@ModelAttribute("searchForm") final SearchForm searchForm, @ModelAttribute("filterForm") final FilterForm filterForm) {
        final ModelAndView mav = new ModelAndView("index");
        List<DoctorView> doctors;
        if (searchForm.getQuery() != null && !searchForm.getQuery().isEmpty()) {
            doctors = dds.findDoctorsByName(searchForm.getQuery());
        } else {
            doctors = dds.getAllDoctors();
        }
        mav.addObject("docList", doctors );
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdays", List.of(WeekdayEnum.values()));
        mav.addObject("specialty", List.of(SpecialtyEnum.values()));
        return mav;
    }

    @RequestMapping("/filter")
    public ModelAndView filter(@ModelAttribute("searchForm") final SearchForm searchForm, @ModelAttribute("filterForm") final FilterForm filterForm){
        ModelAndView mav = new ModelAndView("index");
        List<DoctorView> doctors = dds.getFilteredDoctor(filterForm.getSpecialty(), filterForm.getInsurances(), filterForm.getWeekday());
        mav.addObject("docList", doctors);
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdays", List.of(WeekdayEnum.values()));
        mav.addObject("specialty", List.of(SpecialtyEnum.values()));
        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(@ModelAttribute("searchForm") final SearchForm searchForm, @ModelAttribute("filterForm") final FilterForm filterForm) {
        ModelAndView mav = new ModelAndView("index");
        List<DoctorView> doctors = dds.findDoctorsByName(searchForm.getQuery());
        mav.addObject("docList", doctors);
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdays", List.of(WeekdayEnum.values()));
        mav.addObject("specialty", List.of(SpecialtyEnum.values()));
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