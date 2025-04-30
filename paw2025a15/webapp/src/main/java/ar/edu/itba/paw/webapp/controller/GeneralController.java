package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.WeekdayEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class GeneralController {

    private final static int PAGE_SIZE = 8;

    @Autowired
    private UserService us;

    @Autowired
    private DoctorDetailService dds;
    
    @Autowired
    private InsuranceService is;

    @Autowired
    private MessageSource messageSource;

    private ModelAndView renderLandingPage(
        User user,
        List<DoctorView> doctors,
        List<User> patients,
        int page,
        int totalLength,
        Locale locale
    ) {
        final ModelAndView mav = new ModelAndView("index");

        if(user != null) {
            mav.addObject("user", user);
        }

        if(patients != null) {
            mav.addObject("patients", patients);
        }
        if(doctors != null) {
            mav.addObject("docList", doctors);
        }
        mav.addObject("page", page);
        mav.addObject("totalPages", totalLength / PAGE_SIZE);
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));

        return mav;
    }

    @RequestMapping(value = {"/home", "/home/", "/"}, method = RequestMethod.GET)
    public ModelAndView index (
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        @RequestParam(defaultValue = "1") int page,
        Locale locale
    ) {
        final User user = us.getCurrentUser().orElse(null);

        if(user != null && null != user.getRole()) switch (user.getRole()) {
            case PATIENT -> {
                return renderLandingPage(user, dds.getDoctorsPage(page, PAGE_SIZE), null, page, dds.getTotalDoctors(), locale);
            }
            case DOCTOR -> {
                final List<User> patients = us.getAuthPatientsPageByDoctorId(user.getId(), page, PAGE_SIZE);
                return renderLandingPage(user, null, patients, page, us.getAuthPatientsCountByDoctorId(user.getId()), locale);
            }
            case LABORATORY -> {
                final List<User> patients = us.getAuthPatientsPageByDoctorId(user.getId(), page, PAGE_SIZE);
                return renderLandingPage(user, null, patients, page, us.getAuthPatientsCountByDoctorId(user.getId()), locale);
            }
            case ADMIN -> {
                return renderLandingPage(user, dds.getDoctorsPage(page, PAGE_SIZE), null, page, dds.getTotalDoctors(), locale);
            }
        }

        return renderLandingPage(user, dds.getDoctorsPage(page, PAGE_SIZE), null, page, dds.getTotalDoctors(), locale);
    }

    @RequestMapping("/filter")
    public ModelAndView filter(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        @RequestParam(defaultValue = "1") int page,
        Locale locale
    ){
        final User user = us.getCurrentUser().orElse(null);

        Insurance insurance;
        SpecialtyEnum specialty = filterForm.getSpecialty();
        WeekdayEnum weekday = filterForm.getWeekday();
        if (filterForm.getInsurances() != null) {
            insurance = is.getInsuranceById(filterForm.getInsurances()).orElse(null);
        } else {
            insurance = null;
        }

        List<DoctorView> doctors = dds.getFilteredDoctorsPage(specialty, insurance, weekday, page, PAGE_SIZE);

        return renderLandingPage(user, doctors, null, page, dds.getTotalFilteredDoctors(specialty, insurance, weekday), locale);
    }

    @RequestMapping("/doctorSearch")
    public ModelAndView search(
            @ModelAttribute("searchForm") final SearchForm searchForm,
            @ModelAttribute("filterForm") final FilterForm filterForm,
            @RequestParam(defaultValue = "1") int page,
            Locale locale
    ) {
        final User user = us.getCurrentUser().orElse(null);
        final List<DoctorView> doctors = dds.findDoctorsPageByName(searchForm.getQuery(), page, PAGE_SIZE);

        return renderLandingPage(user, doctors, null, page, dds.getTotalDoctorsByName(searchForm.getQuery()), locale);
    }

    @RequestMapping("/patientSearch")
    public ModelAndView searchPatient(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @RequestParam(defaultValue = "1") int page,
        Locale locale
    ) {
        User user = us.getCurrentUser().orElse(null);
        
        List<User> patients = us.searchAuthPatientsPageByDoctorIdAndName(user.getId(), searchForm.getQuery(), page, PAGE_SIZE);
        return renderLandingPage(user, null, patients, 1, us.searchAuthPatientsCountByDoctorIdAndName(user.getId(), searchForm.getQuery()), locale);
    }
}