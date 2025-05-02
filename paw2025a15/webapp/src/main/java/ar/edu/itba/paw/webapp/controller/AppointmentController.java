package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsWeekForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;


@Controller
public class AppointmentController {

    @Autowired
    private UserService us;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);

    @RequestMapping("/appointments")
    public ModelAndView patientProfile(
        @RequestParam(value = "action", required = false) String action,
        @ModelAttribute("searchForm") final SearchForm searchForm,
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
            
        return mav;        
    }

    @RequestMapping(value = "/cancelAppointment/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(@PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        User user = us.getCurrentUser();

        as.cancelAppointment(shiftId, date, user.getId());
        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/takeAppointment", method = RequestMethod.POST)
    public ModelAndView takeAppointment(@Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        User user = us.getCurrentUser();
        
        as.addAppointment(form.getShiftId(), user.getId(), LocalDate.parse(form.getDate()));

        return new ModelAndView("redirect:/appointments");
    }

    @RequestMapping(value = "/removeAppointment/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView removeAppointment(
        @PathVariable("shiftId") long shiftId,
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        User user = us.getCurrentUser();
        as.removeAppointment(shiftId, date, user.getId());
        
        return new ModelAndView("redirect:/appointments");
    }

    @Scheduled(cron = "0 0 3 * * ?", zone = "America/Argentina/Buenos_Aires")
    public void clearRemovedAppointmentBeforeDate() {
        LocalDate date = LocalDate.now().minusDays(1);
        as.clearRemovedAppointmentBeforeDate(date);
        LOGGER.info("Removed appointments before " + date + " cleared. At " + LocalDateTime.now().toLocalTime());
    }
}
