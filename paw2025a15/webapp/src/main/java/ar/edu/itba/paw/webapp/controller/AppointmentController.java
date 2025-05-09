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
import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.form.ShiftsWeekForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.FormErrorException;

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
            }
            default -> { return new ModelAndView("redirect:/login"); }
        }
        
        mav.addObject("landingForm", new LandingForm());
        mav.addObject("appointmentForm", new AppointmentForm());
        mav.addObject("takeTurnForm", new TakeTurnForm());

        return mav;
    }

    @RequestMapping(value = "/cancelAppointment", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @Valid @ModelAttribute("appointmentForm") final AppointmentForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new FormErrorException("Error in appointment form: " + errors.getFieldErrors().toString());
        }

        try {
            User user = us.getCurrentUser();
            as.cancelAppointment(form.getShiftId(), form.getDate(), user.getId());
        } catch (Exception e) {
            throw new FormErrorException("Error in appointment form: " + e.getMessage());
        }

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors
    ) {
        if(errors.hasErrors()) {
            throw new FormErrorException("Error in appointment form: " + errors.getFieldErrors().toString());
        }

        User user = us.getCurrentUser();
        try {
            as.addAppointment(form.getShiftId(), user.getId(), form.getDate());
        } catch (Exception e) {
            throw new FormErrorException("Error in appointment form: " + e.getMessage());
        }

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/removeAppointment", method = RequestMethod.POST)
    public ModelAndView removeAppointment(
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new FormErrorException("Error in takeTurn form: " + errors.getFieldErrors().toString());
        }

        User user = us.getCurrentUser();
        try {
            as.removeAppointment(form.getShiftId(), form.getDate(), user.getId());
        } catch (Exception e) {
            throw new FormErrorException("Error in form: " + e.getMessage());
        }
        
        return new ModelAndView("redirect:/appointments");
    }
}
