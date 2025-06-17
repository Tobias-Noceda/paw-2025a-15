package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.print.Doc;
import javax.validation.Valid;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.exceptions.FormErrorException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private DoctorService ds;


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
            mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(user.getId()));
            mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(user.getId()));
        } else if (user instanceof Doctor) {
            mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(user.getId()));
            LocalDate date = shiftsDayForm.getDate() != null ? shiftsDayForm.getDate() : LocalDate.now();
            mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByDate(user.getId(), date));
            mav.addObject("shiftsDayForm", shiftsDayForm);
            mav.addObject("date", date);
            mav.addObject("today", LocalDate.now());
            mav.addObject("maxDate", LocalDate.now().plusDays(60));
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
                as.addAppointment(form.getShiftId(), user.getId(), form.getDate(), form.getStartTime(), form.getEndTime(), form.getDetail());
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


    @RequestMapping(value = "/vacations")
    public ModelAndView vacations(
            @ModelAttribute("vacationForm") VacationForm vacationForm,
            BindingResult result,   
            @ModelAttribute("user_data") User user,
            Locale locale
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        Doctor doctor = ds.getDoctorById(user.getId())
                .orElseThrow(() -> new NotFoundException("Doctor does not exist"));
        List<DoctorVacation> vacations = doctor.getVacations();

        ModelAndView mav = new ModelAndView("vacations");
        // Esto es lo que te faltaba

        mav.addObject("futureVacations", ds.getDoctorVacationsFuture(user.getId()));
        mav.addObject("pastVacations", ds.getDoctorVacationsPast(user.getId()));
        // Para que header.jsp no rompa
        mav.addObject("landingForm", new LandingForm());
        vacationForm.setDoctorId(user.getId());
        return mav;
    }

    @RequestMapping(value="/createVacations", method = RequestMethod.POST)
    public ModelAndView createVacation(
            @Valid @ModelAttribute("vacationForm") final VacationForm vacationForm,
            BindingResult result,
            @ModelAttribute("user_data") User user,
            Locale locale
           ) {

        if(result.hasErrors()) {
            ModelAndView mav = new ModelAndView("vacations");

            Doctor doctor = ds.getDoctorById(user.getId())
                    .orElseThrow(() -> new NotFoundException("Doctor does not exist"));

            mav.addObject("futureVacations", ds.getDoctorVacationsFuture(user.getId()));
            mav.addObject("pastVacations", ds.getDoctorVacationsPast(user.getId()));
            mav.addObject("vacations", doctor.getVacations());
            mav.addObject("landingForm", new LandingForm()); // si lo necesita el header

            return mav;
        }

        ds.createDoctorVacation(user.getId(), vacationForm.getStartDate(), vacationForm.getEndDate());

        return new ModelAndView("redirect:/vacations");
    }

    @RequestMapping(value="/removeVacation", method = RequestMethod.POST)
    public ModelAndView removeAppointments(
            @ModelAttribute("user_data") User user,
            Locale locale
           ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        //ds.deleteDoctorVacation(user.getId(), );
        return new ModelAndView("redirect:/vacations");
    }
}

