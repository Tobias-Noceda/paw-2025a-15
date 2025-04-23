package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsMonthForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class AppointmentController {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService us;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    @RequestMapping("/appointments")
    public ModelAndView patientProfile(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
        Locale locale
    ) {
        ModelAndView mav = new ModelAndView("appointments");

        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            mav.addObject("user", user);
            if(user != null && user.getRole() == UserRoleEnum.ROLE_DOCTOR) {
                mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(user.getId()));
                mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByMonth(user.getId(), shiftsMonthForm.getMonth()));

                mav.addObject("shiftsMonthForm", shiftsMonthForm);
                mav.addObject("possibleMonths", getNextThreeMonths(locale));
            } else if(user != null && user.getRole() == UserRoleEnum.ROLE_PATIENT) {
                mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(user.getId()));
                mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(user.getId()));
            } else {
                // TODO: throw unauthorized exception
                return new ModelAndView("redirect:/");
            }
            
            return mav;
        } catch (Exception e) {
        }
        
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/patientCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView patientCancelAppointment(@PathVariable("id") long id, @PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/doctorCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @PathVariable("id") long id,
        @PathVariable("shiftId") long shiftId,
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(@Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        us.getUserByEmail(userDetails.getUsername())
            .ifPresentOrElse(
                user -> { as.addAppointment(form.getShiftId(), user.getId(), LocalDate.parse(form.getDate())); },
                () -> { throw new IllegalArgumentException("User not found"); }
            );

        return new ModelAndView("redirect:/appointments");
    }

    private List<SelectItem> getNextThreeMonths(Locale locale) {
        final List<SelectItem> months = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            LocalDate nextMonth = currentDate.plusMonths(i);
            months.add(new SelectItem(
                nextMonth.getMonth().name(),
                messageSource.getMessage("month." + nextMonth.getMonth().name(), null, locale) + " " + nextMonth.getYear()
            ));
        }
        return months;
    }
}
