package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Locale;

import ar.edu.itba.paw.interfaces.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.form.ShiftsWeekForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Controller
public class DoctorController {

    @Autowired
    private UserService us;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private AuthDoctorService ads;

    @Autowired
    private AuthStudiesService ass;

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") long id,
            @RequestParam(value = "action", required = false) String action,
            @ModelAttribute("shiftsWeekForm") final ShiftsWeekForm shiftsWeekForm,
            @ModelAttribute("takeTurnForm") final TakeTurnForm form,
            Locale locale
    ) {
        DoctorDetail detail = dds.getDetailByDoctorId(id).orElseThrow(() -> new NotFoundException("Doctor not found"));

        final ModelAndView mav = new ModelAndView("doctorDetail");

        User user = us.getUserByEmail(userDetails.getUsername()).orElse(null);
        
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        if (action != null) {
            if ("previous".equals(action)) {
                shiftsWeekForm.decrementIndex();
            } else if ("next".equals(action)) {
                shiftsWeekForm.incrementIndex();
            }
        }
        
        mav.addObject("doctorDetail", detail);
        mav.addObject("isAuthDoctor", ads.hasAuthDoctor(user.getId(), id));
        mav.addObject("allowedAccessLevels", ads.getAuthAccessLevelEnums(user.getId(), id).stream().map(AccessLevelEnum::name).toList());
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances", dds.getDoctorInsurancesById(id));
        mav.addObject("doctorShifts", dss.getUnifiedShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(id, shiftsWeekForm.getMonth(), shiftsWeekForm.getWeekOfMonth()));
        mav.addObject("landingForm", new LandingForm());

        mav.addObject("shiftsWeekForm", shiftsWeekForm);

        return mav;
    }

    @RequestMapping(value = "/patientAuthDoctor/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authUnauthDoctor(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("doctorId") long doctorId,
        @RequestHeader(value = "Referer", required = false) String referer, 
        @RequestParam("action") String action, 
        @RequestParam(value = "accessLevels", required = false) List<String> accessLevels
    ) {
        dds.getDetailByDoctorId(doctorId).orElseThrow(() -> new NotFoundException("Doctor not found"));

        User user = us.getUserByEmail(userDetails.getUsername()).orElse(null);
        
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        if ("update".equals(action)) {
            ads.updateAuthDoctor(user.getId(), doctorId, (accessLevels == null ? null : accessLevels.stream().map(AccessLevelEnum::valueOf).toList()));
        }
        else if ("toggle".equals(action)) {
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