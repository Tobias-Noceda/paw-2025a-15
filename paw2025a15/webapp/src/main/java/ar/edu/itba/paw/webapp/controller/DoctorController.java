package ar.edu.itba.paw.webapp.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsWeekForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;

@Controller
public class DoctorController {

    @Autowired
    private UserService us;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private DoctorCoverageService dcs;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private InsuranceService is;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(
            @PathVariable("id") long id,
            @RequestParam(value = "action", required = false) String action,
            @ModelAttribute("shiftsWeekForm") final ShiftsWeekForm shiftsWeekForm,
            @ModelAttribute("takeTurnForm") final TakeTurnForm form,
            Locale locale
    ) {
        DoctorDetail detail = dds.getDetailByDoctorId(id).orElse(null);

        final ModelAndView mav = new ModelAndView("doctorDetail");

        final User user = us.getCurrentUser();

        if(!user.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to cancel this appointment");
        }
        if (action != null) {
            if ("previous".equals(action)) {
                shiftsWeekForm.decrementIndex();
            } else if ("next".equals(action)) {
                shiftsWeekForm.incrementIndex();
            }
        }
        
        mav.addObject("doctorDetail", detail);
        mav.addObject("user", user);
        mav.addObject("isAuthDoctor", dds.hasAuthDoctor(user.getId(), id));
        mav.addObject("allowedAccessLevels", dds.getAuthAccessLevelEnums(user.getId(), id).stream().map(AccessLevelEnum::name).toList());
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances", dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getUnifiedShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonthAndWeekNumber(id, shiftsWeekForm.getMonth(), shiftsWeekForm.getWeekOfMonth()));
        mav.addObject("searchForm", new SearchForm());

        mav.addObject("shiftsWeekForm", shiftsWeekForm);

        return mav;
    }

    @RequestMapping(value = "/patientAuthDoctor/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authUnauthDoctor(
        @PathVariable("doctorId") long doctorId,
        @RequestHeader(value = "Referer", required = false) String referer, 
        @RequestParam("action") String action, 
        @RequestParam(value = "accessLevels", required = false) List<String> accessLevels) {
        User doctor = us.getUserById(doctorId)
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Doctor not found"));
            
        if(!doctor.getRole().equals(UserRoleEnum.DOCTOR)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Doctor not found");
        }

        User user = us.getCurrentUser();


        if(!user.getRole().equals(UserRoleEnum.PATIENT)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "User not authorized to authorize this doctor");
        }
        
        if ("update".equals(action)) {
            dds.updateAuthDoctor(user.getId(), doctorId, (accessLevels == null ? null : accessLevels.stream().map(AccessLevelEnum::valueOf).toList()));
        }
        else if ("toggle".equals(action)) {
            dds.toggleAuthDoctor(user.getId(), doctorId);
        }

        if (referer != null) {
            return new ModelAndView("redirect:" + referer);
        }

        return new ModelAndView("redirect:/doctors/" + doctorId);        
    }

    @RequestMapping("/register")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form, Locale locale,  @ModelAttribute("registerPatientForm") final PatientForm patientForm) {
        final ModelAndView mav = new ModelAndView("doctorForm");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
        mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
        return mav;
    }
}