package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class GeneralController {

    private final static int PAGE_SIZE = 10;

    @Autowired
    private UserService us;

    @Autowired
    private DoctorDetailService dds;
    
    @Autowired
    private InsuranceService is;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = {"/home", "/home/", "/"}, method = RequestMethod.GET)
    public ModelAndView index (
        @Valid @ModelAttribute("landingForm") final LandingForm landingForm,
        final BindingResult errors,
        @RequestParam(defaultValue = "1") int page,
        Locale locale
    ) {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/home");
        }
        final ModelAndView mav = new ModelAndView("index");
        
        final User user = us.getCurrentUser();
        if(user != null) {
            mav.addObject("user", user);
        }

        int totalLength;
        
        if(user == null || user.getRole().equals(UserRoleEnum.PATIENT) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            Insurance insurance;
            if (landingForm.getInsurances() != null) {
                insurance = is.getInsuranceById(landingForm.getInsurances()).orElse(null);
            } else {
                insurance = null;
            }
            List<DoctorView> doctors = dds.getDoctorsPageByParams(landingForm.getQuery(), landingForm.getSpecialty(), insurance, landingForm.getWeekday(), landingForm.getMostRecent(),landingForm.getMostPopular(),page, PAGE_SIZE);
            totalLength = dds.getTotalDoctorsByParams(landingForm.getEscapedQuery(), landingForm.getSpecialty(), insurance, landingForm.getWeekday());
            mav.addObject("docList", doctors);
        } else {
            List<User> patients = us.getAuthPatientsPageByDoctorIdAndName(user.getId(), landingForm.getQuery(), page, PAGE_SIZE);
            totalLength = us.getAuthPatientsCountByDoctorIdAndName(user.getId(), landingForm.getEscapedQuery());
            mav.addObject("patients", patients);
        }

        int totalPages = (int) Math.ceil((double) totalLength / PAGE_SIZE);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
        mav.addObject("landingForm", landingForm);

        return mav;
    }
}