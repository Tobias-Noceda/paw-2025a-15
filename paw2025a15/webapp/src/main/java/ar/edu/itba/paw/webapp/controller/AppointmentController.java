package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.exceptions.FormErrorException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.form.AppointmentForm;
import ar.edu.itba.paw.webapp.form.LandingForm;
import ar.edu.itba.paw.webapp.form.ShiftsDayForm;
import ar.edu.itba.paw.webapp.form.TakeTurnForm;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    @RequestMapping("/appointments")
    public ModelAndView appointments(
        @ModelAttribute("user_data") User user,
        @RequestParam(value = "action", required = false) String action,
        @ModelAttribute("shiftsDayForm") final ShiftsDayForm shiftsDayForm,
        Locale locale
    ) {
        ModelAndView mav = new ModelAndView("appointments");
        
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        if (user instanceof Patient) {
            System.out.println("Patient appointments");
            mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(user.getId()));
            mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(user.getId()));
        } else if (user instanceof Doctor) {
            System.out.println("Doctor appointments");
            mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(user.getId()));
            mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByDate(user.getId(), LocalDate.now()));
            mav.addObject("shiftsDayForm", shiftsDayForm);
        } else {
            return new ModelAndView("redirect:/login");
        }
        
        mav.addObject("landingForm", new LandingForm());
        mav.addObject("appointmentForm", new AppointmentForm());
        mav.addObject("takeTurnForm", new TakeTurnForm());

        return mav;
    }

    @RequestMapping(value = "/cancelAppointment", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("appointmentForm") final AppointmentForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            throw new FormErrorException("Error in appointment form: " + errors.getFieldErrors().toString());
        }

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        try {
            as.cancelAppointment(form.getShiftId(), form.getDate(), form.getStartTime(), form.getEndTime(), user.getId());
        } catch (Exception e) {
            throw new FormErrorException("Error in appointment form: " + e.getMessage());
        }

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors,
        RedirectAttributes redirectAttributes
    ) {
        //throw new FormErrorException("Error in appointment form: " + errors.getFieldErrors().toString());
        if(!errors.hasErrors()) {
            if (user == null) {
                throw new UnauthorizedException("User not found");
            }
            try {
                as.addAppointment(form.getShiftId(), user.getId(), form.getDate(), form.getStartTime(), form.getEndTime(), ""); // TODO: add detail to the appointment
            } catch (Exception e) {
                throw new FormErrorException("Error in appointment form: " + e.getMessage());
            }
            return new ModelAndView("redirect:/appointments");
        } else {
            redirectAttributes.addFlashAttribute("takeTurnErrors", errors);
            return new ModelAndView("redirect:/doctors/" + form.getDoctorId());
        }
    }

    @RequestMapping(value = "/removeAppointment", method = RequestMethod.POST)
    public ModelAndView removeAppointment(
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors,
        RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("removeError", errors);
            return new ModelAndView("redirect:/appointments");
        }

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        try {
            as.removeAppointment(form.getShiftId(), form.getDate(), user.getId(), form.getStartTime(), form.getEndTime());
        } catch (Exception e) {
            throw new FormErrorException("Error in form: " + e.getMessage());
        }
        
        return new ModelAndView("redirect:/appointments");
    }
}
