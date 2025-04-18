package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
// import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Controller
public class UserController {

    @Autowired
    private UserService us;

    // @Autowired
    // private PatientCoverageService pcs;

    @Autowired
    private DoctorCoverageService dcs;

    @Autowired
    private AppointmentService as;

    // TODO: sacar el ID del usuario de la session and maybe move to an ApointmentsController
    @RequestMapping("/appointments/patient/{id:\\d+}")
    public ModelAndView patientProfile(
        @PathVariable("id") long id,
        @ModelAttribute("searchForm") final SearchForm searchForm
    ){
        ModelAndView mav = new ModelAndView("appointments");
        // User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such patient"));
        // mav.addObject("patient", patient);
        // mav.addObject("patientInsurance", pcs.getInsuranceById(id));
        mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(id));
        mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(id));
        return mav;
    }

    @RequestMapping(value = "/patientCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView patientCancelAppointment(@PathVariable("id") long id, @PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate date){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/appointments/patient/" + id);
    }

    @RequestMapping("/doctorProfile/{id:\\d+}")
    public ModelAndView doctorProfile(@PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("doctorProfile");
        User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such doctor"));
        mav.addObject("doctor", patient);
        mav.addObject("doctorInsurances", dcs.getInsurancesById(id));
        mav.addObject("doctorFutureAppointments", as.getFutureAppointmentDataByDoctorId(id));
        mav.addObject("doctorOldAppointments", as.getOldAppointmentDataByDoctorId(id));
        return mav;
    }

    @RequestMapping(value = "/doctorCancelAppointment/{id:\\d+}/{shiftId:\\d+}/{date}", method = RequestMethod.POST)
    public ModelAndView cancelAppointment(@PathVariable("id") long id, @PathVariable("shiftId") long shiftId, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        as.cancelAppointment(shiftId, date, id);
        return new ModelAndView("redirect:/doctorProfile/" + id);
    }
}
