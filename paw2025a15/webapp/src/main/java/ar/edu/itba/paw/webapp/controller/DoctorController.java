package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;

@Controller
public class DoctorController {

    private final UserService us;
    private final DoctorDetailService dds;
    private final DoctorCoverageService dcs;
    private final DoctorShiftService dss;
    private final InsuranceService is;
    private final AppointmentService as;

    @Autowired
    public DoctorController(final UserService us, final DoctorDetailService dds, final DoctorCoverageService dcs, final DoctorShiftService dss, final InsuranceService is, final AppointmentService as){
        this.us = us;
        this.dds = dds;
        this.dcs = dcs;
        this.dss = dss;
        this.is = is;
        this.as = as;
    }

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("doctor-detail");
        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances" ,dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdAndDate(id, LocalDate.now()));
        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("items", is.getAllInsurances());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        //us.createMedic(form.getName(), form.getEmail());
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("form", form);
        return mav;
    }
}
