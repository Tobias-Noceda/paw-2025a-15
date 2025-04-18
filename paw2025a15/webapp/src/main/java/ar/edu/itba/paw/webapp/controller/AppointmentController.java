package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
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
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;


@Controller
public class AppointmentController {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorShiftService dss;

    @RequestMapping("/appointments/patient/{id:\\d+}")
    public ModelAndView patientProfile(
        @PathVariable("id") long id,
        @ModelAttribute("searchForm") final SearchForm searchForm
    ){
        ModelAndView mav = new ModelAndView("appointments");

        mav.addObject("userType", "PATIENT");
        mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(id));
        mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(id));
        
        return mav;
    }

    @RequestMapping(value = "/patientCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView patientCancelAppointment(@PathVariable("id") long id, @PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments/patient/" + id);
    }

    @RequestMapping("/appointments/doctor/{id:\\d+}")
    public ModelAndView doctorProfile(
        @PathVariable("id") long id,
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("takeTurnForm") final TakeTurnForm takeTurnForm,
        @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
        Locale locale
    ){
        ModelAndView mav = new ModelAndView("appointments");
        
        mav.addObject("userType", "DOCTOR");
        mav.addObject("doctorId", id);
        mav.addObject("doctorTakenAppointments", as.getFutureAppointmentDataByDoctorId(id));
        mav.addObject("doctorFreeAppointments", dss.getAvailableTurnsByDoctorIdByMonth(id, shiftsMonthForm.getMonth()));

        mav.addObject("shiftsMonthForm", shiftsMonthForm);
        mav.addObject("possibleMonths", getNextThreeMonths(locale));

        return mav;
    }

    @RequestMapping(value = "/doctorCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(
        @PathVariable("id") long id,
        @PathVariable("shiftId") long shiftId,
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/doctorProfile/" + id);
    }

    @RequestMapping(value = "/takeAppointment/{takerId:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView takeAppointment(
        @PathVariable("takerId") long takerId,
        @PathVariable("shiftId") long shiftId,
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        as.addAppointment(shiftId, takerId, date);

        // TODO: patient o doctor segun el rol del usuario logueado
        return new ModelAndView("redirect:/appointments/patient/" + takerId);
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
