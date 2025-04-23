package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class GeneralController {

    @Autowired
    private UserService us;

    @Autowired
    private DoctorDetailService dds;
    
    @Autowired
    private InsuranceService is;

    private ModelAndView renderLandingPage(
        User user,
        List<DoctorView> doctors,
        List<User> patients,
        Locale locale
    ) {
        final ModelAndView mav = new ModelAndView("index");

        if(user != null) {
            mav.addObject("user", user);
            mav.addObject("isAuthDoctor", dds.hasAuthDoctor(user.getId(), 0));
        }

        if(patients != null) {
            mav.addObject("patients", patients);
        }
        if(doctors != null) {
            mav.addObject("docList", doctors);
        }
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(locale));

        return mav;
    }

    @RequestMapping(value = {"/home", "/home/", "/"}, method = RequestMethod.GET)
    public ModelAndView index (
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ) {
        final User user = us.getCurrentUser().orElse(null);

        if(null != user.getRole()) switch (user.getRole()) {
            case PATIENT -> {
                return renderLandingPage(user, dds.getAllDoctors(), null, locale);
            }
            case DOCTOR -> {
                return renderLandingPage(user, null, us.getAuthPatientsByDoctorId(user.getId()), locale);
            }
            case LABORATORY -> {
                return renderLandingPage(user, null, us.getAuthPatientsByDoctorId(user.getId()), locale);
            }
            case ADMIN -> {
                return renderLandingPage(user, dds.getAllDoctors(), null, locale);
            }
        }

        return renderLandingPage(user, dds.getAllDoctors(), null, locale);
    }

    @RequestMapping("/filter")
    public ModelAndView filter(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ){
        final User user = us.getCurrentUser().orElse(null);

        Insurance insurance;
        if (filterForm.getInsurances() != null) {
            insurance = is.getInsuranceById(filterForm.getInsurances()).orElse(null);
        } else {
            insurance = null;
        }

        List<DoctorView> doctors = dds.getFilteredDoctor(
                filterForm.getSpecialty(),
                insurance,
                filterForm.getWeekday()
        );

        return renderLandingPage(user, doctors, null, locale);
    }

    @RequestMapping("/doctorSearch")
    public ModelAndView search(
            @ModelAttribute("searchForm") final SearchForm searchForm,
            @ModelAttribute("filterForm") final FilterForm filterForm,
            Locale locale
    ) {
        final User user = us.getCurrentUser().orElse(null);
        final List<DoctorView> doctors = dds.findDoctorsByName(searchForm.getQuery());

        return renderLandingPage(user, doctors, null, locale);
    }

    @RequestMapping("/patientSearch")
    public ModelAndView searchPatient(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        Locale locale
    ) {
        User user = us.getCurrentUser().orElse(null);
            
        return renderLandingPage(user, null, us.searchAuthPatientsByDoctorIdAndName(user.getId(), searchForm.getQuery()), locale);
    }
}