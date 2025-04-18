package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientCoverageService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Controller
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private PatientCoverageService pcs;

    @Autowired
    private DoctorCoverageService dcs;

    @Autowired
    private AppointmentService as;

    @Autowired
    private StudyService ss;

    @Autowired
    private DoctorDetailService dds;

    @RequestMapping("/patientProfile/{id:\\d+}")
    public ModelAndView patientProfile(@PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("patientProfile");
        User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such patient"));
        mav.addObject("patient", patient);
        mav.addObject("patientInsurance", pcs.getInsuranceById(id));
        mav.addObject("patientFutureAppointments", as.getFutureAppointmentDataByPatientId(id));
        mav.addObject("patientOldAppointments", as.getOldAppointmentDataByPatientId(id));
        mav.addObject("patientStudies", ss.getStudiesByPatientId(id));
        mav.addObject("patientAuthDoctors", dds.getAuthDoctorsByPatientId(id));
        return mav;
    }

    @RequestMapping("/doctorProfile/{id:\\d+}")
    public ModelAndView doctorProfile(@PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("doctorProfile");
        User patient = us.getUserById(id).orElseThrow(()->new IllegalArgumentException("No such doctor"));
        mav.addObject("doctor", patient);
        mav.addObject("doctorInsurances", dcs.getInsurancesById(id));
        mav.addObject("doctorFutureAppointments", as.getFutureAppointmentDataByDoctorId(id));
        mav.addObject("doctorOldAppointments", as.getOldAppointmentDataByDoctorId(id));
        mav.addObject("doctorAuthPatients", us.getAuthPatientsByDoctorId(id));
        return mav;
    }
}
