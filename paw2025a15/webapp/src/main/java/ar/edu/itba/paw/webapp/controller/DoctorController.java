package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

    private ModelAndView renderIndexPage(Locale locale) {
        final ModelAndView mav = new ModelAndView("index");

        mav.addObject("insurances", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", getListOfWeekdays(locale));
        mav.addObject("specialtySelectItems", getListOfSpecialties(locale));

        return mav;
    }

    @RequestMapping("/")
    public ModelAndView index(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ) {
        final ModelAndView mav = renderIndexPage(locale);
        List<DoctorView> doctors = dds.getAllDoctors();
        mav.addObject("docList", doctors);
        
        return mav;
    }

    @RequestMapping("/filter")
    public ModelAndView filter(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ){
        ModelAndView mav = renderIndexPage(locale);
        Insurance insurance;
        if(filterForm.getInsurances() != null) {
            insurance = is.getInsuranceById(filterForm.getInsurances()).orElse(null);
        }else{
            insurance = null;
        }

        List<DoctorView> doctors = dds.getFilteredDoctor(
                filterForm.getSpecialty(),
                insurance,
                filterForm.getWeekday()
        );

        mav.addObject("docList", doctors);

        return mav;
    }

    @RequestMapping("/search")
    public ModelAndView search(
        @ModelAttribute("searchForm") final SearchForm searchForm,
        @ModelAttribute("filterForm") final FilterForm filterForm,
        Locale locale
    ) {
        ModelAndView mav = renderIndexPage(locale);
        List<DoctorView> doctors = dds.findDoctorsByName(searchForm.getQuery());
        mav.addObject("docList", doctors);
        return mav;
    }

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(
        @PathVariable("id") long id,
        @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
        @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        Locale locale
    ) {
        final ModelAndView mav = new ModelAndView("doctorDetail");
        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances" ,dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getUnifiedShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonth(id, shiftsMonthForm.getMonth()));
        mav.addObject("searchForm", new SearchForm());

        mav.addObject("shiftsMonthForm", shiftsMonthForm);
        mav.addObject("possibleMonths", getNextThreeMonths(locale));

        return mav;
    }


    @RequestMapping(value = "/doctors/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView takeTurn(
        @PathVariable("id") long id,
        @ModelAttribute("shiftsMonthForm") final ShiftsMonthForm shiftsMonthForm,
        Locale locale,
        @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form,
        final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return doctorProfile(id, new ShiftsMonthForm(), form, locale);
        }
        
        User patient = us.getUserByEmail(form.getEmail())
            .orElseGet(() -> us.create(form.getEmail(), "12345678", form.getName() + " " + form.getSurname()));

        return new ModelAndView("redirect:/takeAppointment/" + patient.getId() + "/" + form.getShiftId() + "/" + form.getDate());
    }

    @RequestMapping(value = "/patientAuthDoctor/{patientId:\\d+}/{doctorId:\\d+}", method = RequestMethod.POST)
    public ModelAndView authUnauthDoctor(@PathVariable("patientId") long patientId, @PathVariable("doctorId") long doctorId) {
        dds.toggleAuthDoctor(patientId, doctorId);
        return new ModelAndView("redirect:/patientProfile/" + patientId);
    }

    @RequestMapping("/doctor-form")
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

        // Si no hay errores, proceder con la creación del médico
        User doc = us.createDoctor(form.getEmail(), "12345678", form.getName() + " " + form.getSurname(), "med-licence", form.getSpeciality()); //TODO magicnumber password sacar y getLicence
        dcs.addCoverages(doc.getId(), form.getObrasSociales());
        dss.createShifts(doc.getId(), form.getSchedules().getWeekday(), form.getAddress(), LocalTime.parse(form.getSchedules().getStartTime()), LocalTime.parse(form.getSchedules().getEndTime()), form.getAmount());
        ModelAndView mav = new ModelAndView("redirect:/");

        return mav;
    }

    private List<SelectItem> getHoursSelectItems() {
        final List<SelectItem> times = new ArrayList<>();
        for(Integer hour = 6;hour < 22;hour++){
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            if(hour < 10){
                a.append("0");
                b.append("0");
            }
            a.append(hour).append(":00");
            b.append(hour).append(":30");
            times.add(new SelectItem(a.toString(), a.toString()));
            times.add(new SelectItem((b.toString()),b.toString()));
        }
        return times;
    }

    private List<SelectItem> getListOfSpecialties(Locale locale) {
        final List<SelectItem> specialties = new ArrayList<>();
        // For each specialty, create a SelectItem and add it to the list
        for (SpecialtyEnum specialty : SpecialtyEnum.values()) {
            specialties.add(new SelectItem(specialty.name(), messageSource.getMessage("specialty." + specialty.name(), null, locale)));
        }

        return specialties;
    }

    private List<SelectItem> getListOfWeekdays(Locale locale) {
        final List<SelectItem> weekdays = new ArrayList<>();
        // For each specialty, create a SelectItem and add it to the list
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
