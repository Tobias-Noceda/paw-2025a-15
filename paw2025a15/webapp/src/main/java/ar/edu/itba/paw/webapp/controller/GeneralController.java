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

import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;
import ar.edu.itba.paw.webapp.form.LandingForm;


@Controller
public class GeneralController {

    private final static int PAGE_SIZE = 10;

    @Autowired
    private DoctorDetailService dds;
    
    @Autowired
    private InsuranceService is;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = {"/home", "/home/", "/"}, method = RequestMethod.GET)
    public ModelAndView index (
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("landingForm") final LandingForm landingForm,
        final BindingResult errors,
        @RequestParam(defaultValue = "1") int page,
        Locale locale
    ) {
        if(errors.hasErrors()) {
            return new ModelAndView("redirect:/home");
        }
        final ModelAndView mav = new ModelAndView("index");

        int totalLength;
        
        if(user == null || user.getRole().equals(UserRoleEnum.PATIENT) || user.getRole().equals(UserRoleEnum.ADMIN)) {
            Long insuranceId;
            if (landingForm.getInsurances() != null) {
                if(is.getInsuranceById(landingForm.getInsurances()).isPresent()) {
                    insuranceId = landingForm.getInsurances();
                } else {
                    insuranceId = null;
                }
            } else {
                insuranceId = null;
            }
            List<Doctor> doctors = dds.getDoctorsPageByParams(landingForm.getQuery(), landingForm.getSpecialty(), insuranceId, landingForm.getWeekday(), landingForm.getOrderBy(),page, PAGE_SIZE);
            totalLength = dds.getTotalDoctorsByParams(landingForm.getQuery(), landingForm.getSpecialty(), insuranceId, landingForm.getWeekday());
            mav.addObject("docList", doctors);
        } else {
            List<Patient> patients = dds.getAuthPatientsPageByDoctorIdAndName(user.getId(), landingForm.getQuery(), page, PAGE_SIZE);
            totalLength = dds.getAuthPatientsCountByDoctorIdAndName(user.getId(), landingForm.getQuery());
            mav.addObject("patients", patients);
        }

        int totalPages = (int) Math.ceil((double) totalLength / PAGE_SIZE);
        mav.addObject("page", page);
        mav.addObject("totalPages", totalPages);
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
        mav.addObject("orderSelectItems", SelectItem.getDoctorOrderSelectItems(messageSource, locale));
        mav.addObject("landingForm", landingForm);

        return mav;
    }
}