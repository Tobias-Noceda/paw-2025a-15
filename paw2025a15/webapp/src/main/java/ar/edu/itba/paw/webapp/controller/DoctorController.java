package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.FilterForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.ShiftsMonthForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.WeekdayEnum;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;

@Controller
public class DoctorController {

    @Autowired
    private MessageSource messageSource;

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
    private PasswordEncoder passwordEncoder;

    private ModelAndView renderLandingPage(List<DoctorView> doctors, List<User> patients, Locale locale) {
        
        final ModelAndView mav = new ModelAndView("index");
        
        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())//TODO this sounds weird
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            mav.addObject("user", user);
        } catch (Exception e) {
            
        }

        if(patients != null) {
            mav.addObject("patients", patients);
        }
        if(doctors != null) {
            mav.addObject("docList", doctors);
        }
        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
        mav.addObject("specialtySelectItems", getListOfSpecialties(locale));

        return mav;
    }

    @RequestMapping(value = {"/home", "/home/", "/"}, method = RequestMethod.GET)
    public ModelAndView index (
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ) {
        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            // Si llegó hasta acá, está logueado
            final User user = us.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            if(null != user.getRole()) switch (user.getRole()) {
                case PATIENT -> {
                    return renderLandingPage(dds.getAllDoctors(), null, locale);
                }
                case DOCTOR -> {
                    return renderLandingPage(null, us.getAuthPatientsByDoctorId(user.getId()), locale);
                }
                case LABORATORY -> {
                    return renderLandingPage(null, us.getAuthPatientsByDoctorId(user.getId()), locale);
                }
                default -> {
                }
            }
        } catch (ClassCastException e) {
            return renderLandingPage(dds.getAllDoctors(), null, locale);
        }
        
        return renderLandingPage(dds.getAllDoctors(), null, locale);
    }

    @RequestMapping("/filter")
    public ModelAndView filter(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ){
        Insurance insurance;
        if (filterForm.getInsurances() != null) {
            insurance = is.getInsuranceById(filterForm.getInsurances()).orElse(null);
        } else {
            insurance = null;
        }

        List<DoctorView> doctors = dds.getFilteredDoctor(
                filterForm.getSpecialty(),
                insurance,
                filterForm.getWeekday()
        );

        return renderLandingPage(doctors, null, locale);
    }

    @RequestMapping("/doctorSearch")
    public ModelAndView search(
            @ModelAttribute("searchForm") final SearchForm searchForm,
            @ModelAttribute("filterForm") final FilterForm filterForm,
            Locale locale
    ) {
        return renderLandingPage(dds.findDoctorsByName(searchForm.getQuery()), null, locale);
    }

    @RequestMapping("/patientSearch")
    public ModelAndView searchPatient(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        Locale locale
    ) {
        try {
            final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
            
            final User user = us.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
            
            return renderLandingPage(null, us.searchAuthPatientsByDoctorIdAndName(user.getId(), searchForm.getQuery()), locale);
        } catch (Exception e) {
        }

        return new ModelAndView("redirect:/home");
    }

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
        mav.addObject("possibleMonths", getNextThreeMonths(locale));

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
        mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
        mav.addObject("specialtySelectItems", getListOfSpecialties(locale));
        mav.addObject("hoursSelectItems", getHoursSelectItems());
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerMedicForm") final DoctorForm form,
            final BindingResult errors,
            Locale locale,
            @ModelAttribute("filterForm") final FilterForm filterForm
    ) {
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
            mav.addObject("specialtySelectItems", getListOfSpecialties(locale));
            mav.addObject("hoursSelectItems", getHoursSelectItems());
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        // Validar que las contraseñas coincidan
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.passwordMismatch");
            ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
            mav.addObject("specialtySelectItems", getListOfSpecialties(locale));
            mav.addObject("hoursSelectItems", getHoursSelectItems());
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        // Verificar si el email ya existe
        if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists");
            ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
            mav.addObject("specialtySelectItems", getListOfSpecialties(locale));
            mav.addObject("hoursSelectItems", getHoursSelectItems());
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        try {
            // Crear el médico
            User doc = us.createDoctor(form.getEmail(), passwordEncoder.encode(form.getPassword()), form.getName() + " " + form.getSurname(), form.getPhoneNumber(), "med-licence", form.getSpeciality());
            dcs.addCoverages(doc.getId(), form.getObrasSociales());
            dss.createShifts(doc.getId(), form.getSchedules().getWeekday(), form.getAddress(), LocalTime.parse(form.getSchedules().getStartTime()), LocalTime.parse(form.getSchedules().getEndTime()), form.getAmount());
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            errors.reject("error.registerDoctorFailed");
            ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
            mav.addObject("specialtySelectItems", getListOfSpecialties(locale));
            mav.addObject("hoursSelectItems", getHoursSelectItems());
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }
    }

    private List<SelectItem> getHoursSelectItems() {
        final List<SelectItem> times = new ArrayList<>();
        for(Integer hour = 6; hour < 22; hour++) {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            if (hour < 10) {
                a.append("0");
                b.append("0");
            }
            a.append(hour).append(":00");
            b.append(hour).append(":30");
            times.add(new SelectItem(a.toString(), a.toString()));
            times.add(new SelectItem((b.toString()), b.toString()));
        }
        return times;
    }

    private List<SelectItem> getListOfSpecialties(Locale locale) {
        final List<SelectItem> specialties = new ArrayList<>();
        for (SpecialtyEnum specialty : SpecialtyEnum.values()) {
            specialties.add(new SelectItem(specialty.name(), messageSource.getMessage("specialty." + specialty.name(), null, locale)));
        }
        return specialties;
    }

    private List<SelectItem> getListOfWeekdays(Locale locale) {
        final List<SelectItem> weekdays = new ArrayList<>();
        for (WeekdayEnum weekday : WeekdayEnum.values()) {
            weekdays.add(new SelectItem(weekday.name(), messageSource.getMessage("weekday." + weekday.name(), null, locale)));
        }
        return weekdays;
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