package ar.edu.itba.paw.webapp.controller;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.AppointmentForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsWeekForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.NotFoundException;


@Controller
public class AppointmentController {

    @Autowired
    private UserService us;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;


    @RequestMapping("/appointments")
    public ModelAndView appointments(
        @RequestParam(value = "action", required = false) String action,
        @ModelAttribute("shiftsWeekForm") final ShiftsWeekForm shiftsWeekForm,
        Locale locale
    ) {
        ModelAndView mav = new ModelAndView("appointments");

        User user = us.getCurrentUser();
        
        mav.addObject("user", user);
        switch (user.getRole()) {
            case DOCTOR -> {
                mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(user.getId()));
                if (action != null) {
                    if ("previous".equals(action)) {
                        shiftsWeekForm.decrementIndex();
                    } else if ("next".equals(action)) {
                        shiftsWeekForm.incrementIndex();
                    }
                }
                mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(user.getId(), shiftsWeekForm.getMonth(), shiftsWeekForm.getWeekOfMonth()));
                mav.addObject("shiftsWeekForm", shiftsWeekForm);
            }
            case PATIENT -> {
                mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(user.getId()));
                mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(user.getId()));
                mav.addObject("takeTurnForm", new TakeTurnForm());
            }
            default -> { return new ModelAndView("redirect:/login"); }
        }
        
        mav.addObject("searchForm", new SearchForm());
        mav.addObject("appointmentForm", new AppointmentForm());
        return mav;
    }

    @RequestMapping(value = "/cancelAppointment", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @Valid @ModelAttribute("appointmentForm") final AppointmentForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new NotFoundException("Error in appointment form");
        }

        try {
            User user = us.getCurrentUser();
            as.cancelAppointment(form.getShiftId(), form.getDate(), user.getId());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error in appointment form");
        }

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors
    ) {
        if(errors.hasErrors()) {
            throw new NotFoundException("Error in appointment form");
        }

        User user = us.getCurrentUser();
        
        
        try {
            as.addAppointment(form.getShiftId(), user.getId(), form.getDate());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error in appointment form");
        }

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/removeAppointment", method = RequestMethod.POST)
    public ModelAndView removeAppointment(
        @Valid @ModelAttribute("appointmentForm") final AppointmentForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new NotFoundException("Error in appointment form");
        }
        User user = us.getCurrentUser();
        try {
            as.removeAppointment(form.getShiftId(), form.getDate(), user.getId());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Error in appointment form");
        }
        
        return new ModelAndView("redirect:/appointments");
    }
}
