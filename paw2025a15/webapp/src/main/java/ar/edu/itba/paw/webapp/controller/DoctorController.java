package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.form.LandingForm;
import ar.edu.itba.paw.webapp.form.ShiftsDayForm;
import ar.edu.itba.paw.webapp.form.TakeTurnForm;

@Controller
public class DoctorController {

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private AuthDoctorService ads;

    @Autowired
    private AuthStudiesService ass;

    @Autowired
    private DoctorShiftService dss;

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(
            @ModelAttribute("user_data") User user,
            @PathVariable("id") long id,
            @RequestParam(value = "action", required = false) String action,
            @ModelAttribute("shiftsDayForm") final ShiftsDayForm shiftsDayForm,
            @ModelAttribute("takeTurnForm") final TakeTurnForm form,
            Locale locale
    ) {
        Doctor doctor = dds.getDoctorById(id).orElseThrow(() -> new NotFoundException("Doctor does not exist"));

        final ModelAndView mav = new ModelAndView("doctorDetail");

        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        mav.addObject("today", LocalDate.now());
        mav.addObject("maxDate", LocalDate.now().plusDays(60));
        mav.addObject("doctor", doctor);
        mav.addObject("isAuthDoctor", ads.hasAuthDoctor(user.getId(), id));
        mav.addObject("allowedAccessLevels", ads.getAuthAccessLevelEnums(user.getId(), id).stream().map(AccessLevelEnum::name).toList());
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByDate(id, shiftsDayForm.getDate()));
        mav.addObject("date", shiftsDayForm.getDate());

        mav.addObject("landingForm", new LandingForm());
        mav.addObject("shiftsDayForm", shiftsDayForm);
        
        return mav;
    }

    @RequestMapping(value = "/patientAuthDoctor/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authUnauthDoctor(
        @ModelAttribute("user_data") User user,
        @PathVariable("doctorId") long doctorId,
        @RequestHeader(value = "Referer", required = false) String referer, 
        @RequestParam("action") String action, 
        @RequestParam(value = "accessLevels", required = false) List<String> accessLevels
    ) {
        dds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor not found"));
        
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        if (action.equals("update")) {
            ads.updateAuthDoctor(user.getId(), doctorId, (accessLevels == null ? null : accessLevels.stream().map(AccessLevelEnum::valueOf).toList()));
        }
        else if (action.equals("toggle")) {
            ads.toggleAuthDoctor(user.getId(), doctorId);
        }

        if (referer != null) {
            return new ModelAndView("redirect:" + referer);
        }

        return new ModelAndView("redirect:/doctors/" + doctorId);        
    }

    @RequestMapping(value = "/authFileDoctor/{doctorId:\\d+}/{studyId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authDoctorToFile(
            @PathVariable("doctorId") long doctorId,
            @PathVariable("studyId") long studyId
    ){
        ass.toggleStudyForDoctorId(studyId, doctorId);
        return new ModelAndView("redirect:/study-info/" + studyId);
    }
}