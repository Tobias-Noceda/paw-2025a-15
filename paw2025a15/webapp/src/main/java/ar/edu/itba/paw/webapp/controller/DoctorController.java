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
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.WeekdayEnum;

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
    private AppointmentService as;

    @Autowired
    private InsuranceService is;

    @Autowired
    private EmailService es;

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
        List<DoctorView> doctors;
        // if (searchForm.getQuery() != null && !searchForm.getQuery().isEmpty()) {
        //     doctors = dds.findDoctorsByName(searchForm.getQuery());
        // } else {
            doctors = dds.getAllDoctors();
        //}
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
    public ModelAndView doctorProfile(@PathVariable("id") long id, @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        final ModelAndView mav = new ModelAndView("doctorDetail");
        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances" ,dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getUnifiedShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonth(id, LocalDate.now().getMonth()));
        mav.addObject("doctorId", id);
        mav.addObject("searchForm", new SearchForm());

        return mav;
    }


    @RequestMapping(value = "/doctors/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView takeTurn(@PathVariable("id") long id, @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form, final BindingResult errors) {
        final ModelAndView mav = new ModelAndView("redirect:/");
        if (errors.hasErrors()) {
            return doctorProfile(id, form);
        }

        User patient = us.getUserByEmail(form.getEmail())
            .orElseGet(() -> us.create(form.getEmail(), "12345678", form.getName() + " " + form.getSurname()));
        Appointment appointment = as.addAppointment(form.getShiftId(), patient.getId(), LocalDate.parse(form.getDate()));
        return mav;
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
        dss.createShifts(doc.getId(), form.getSchedules().getWeekday(), form.getAddress(), LocalTime.parse(form.getSchedules().getStartTime()), LocalTime.parse(form.getSchedules().getEndTime()), form.getAmount());//TODO change Schedule model or sth.
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

    protected class SelectItem {
        private String value;
        private String label;

        // Constructor, getters y setters
        public SelectItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
