package ar.edu.itba.paw.webapp.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsMonthForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
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
            @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
            @ModelAttribute("takeTurnForm") final TakeTurnForm form,
            Locale locale
    ) {
        final ModelAndView mav = new ModelAndView("doctorDetail");
       
        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            mav.addObject("user", user);
            mav.addObject("isAuthDoctor", dds.hasAuthDoctor(user.getId(), id));
        } catch (Exception e) {
        }

        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances", dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getUnifiedShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonth(id, shiftsMonthForm.getMonth()));
        mav.addObject("searchForm", new SearchForm());

        mav.addObject("shiftsMonthForm", shiftsMonthForm);
        mav.addObject("possibleMonths", SelectItem.getNextThreeMonths(messageSource, locale));

        return mav;
    }

    @RequestMapping(value = "/patientAuthDoctor/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authUnauthDoctor(@PathVariable("doctorId") long doctorId) {
        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            dds.toggleAuthDoctor(user.getId(), doctorId);
        } catch (Exception e) {
        }

        return new ModelAndView("redirect:/doctors/" + doctorId);
    }

    @RequestMapping("/register/doctor-form")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form, Locale locale) {
        final ModelAndView mav = new ModelAndView("doctorForm");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
        mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
        return mav;
    }
}