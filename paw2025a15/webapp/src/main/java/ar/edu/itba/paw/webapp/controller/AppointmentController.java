package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsMonthForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class AppointmentController {

    @Autowired
    private UserService us;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/appointments")
    public ModelAndView patientProfile(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
        Locale locale
    ) {
        ModelAndView mav = new ModelAndView("appointments");

        User user = us.getCurrentUser().orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found or not authorized to view appointments"));  
        
        mav.addObject("user", user);
        switch (user.getRole()) {
            case DOCTOR -> {
                mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(user.getId()));
                mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByMonth(user.getId(), shiftsMonthForm.getMonth()));
                mav.addObject("shiftsMonthForm", shiftsMonthForm);
                mav.addObject("possibleMonths", SelectItem.getNextThreeMonths(messageSource, locale));
            }
            case PATIENT -> {
                mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(user.getId()));
                mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(user.getId()));
            }
            default -> throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to view appointments");
        }
            
        return mav;        
    }

    @RequestMapping(value = "/patientCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView patientCancelAppointment(@PathVariable("id") long id, @PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        User user = us.getCurrentUser().orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found or not authorized to cancel this appointment"));

        if(!user.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to cancel this appointment");
        }

        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/doctorCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @PathVariable("id") long id,
        @PathVariable("shiftId") long shiftId,
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        User user = us.getCurrentUser().orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found or not authorized to cancel this appointment"));
        
        if(!user.getRole().equals(UserRoleEnum.DOCTOR)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to cancel this appointment");
        }

        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(@Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        User user = us.getCurrentUser().orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found or not authorized to cancel this appointment"));
        
        as.addAppointment(form.getShiftId(), user.getId(), LocalDate.parse(form.getDate()));

        return new ModelAndView("redirect:/appointments");
    }
}
