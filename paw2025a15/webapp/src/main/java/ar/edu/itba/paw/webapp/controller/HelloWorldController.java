package ar.edu.itba.paw.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;


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
    public ModelAndView filter(@ModelAttribute("searchForm") final SearchForm searchForm,
                               @ModelAttribute("filterForm") final FilterForm filterForm){
        ModelAndView mav = new ModelAndView("index");
        Insurance insurance;
        if(filterForm.getInsurances() != null) {
            insurance = is.getInsuranceById(filterForm.getInsurances()).orElse(null);
        }else{
            insurance = null;
        }




        List<DoctorView> doctors = dds.getFilteredDoctor(
                filterForm.getSpecialty(),
                insurance,
                filterForm.getWeekday()
        );

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