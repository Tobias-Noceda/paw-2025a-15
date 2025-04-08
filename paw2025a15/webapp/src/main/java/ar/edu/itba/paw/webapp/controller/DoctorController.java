package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import ar.edu.itba.paw.form.FilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.WeekdayEnum;

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
    private AppointmentService as;

    @Autowired
    private InsuranceService is;

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

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(@PathVariable("id") long id, @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        final ModelAndView mav = new ModelAndView("doctor-detail");
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
        as.addApointment(form.getShiftId(), patient.getId(), LocalDate.parse(form.getDate()));
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", List.of(WeekdayEnum.values()));
        mav.addObject("hoursSelectItems", getHoursSelectItems());
        mav.addObject("searchForm", new SearchForm());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerMedicForm") final DoctorForm form,
            final BindingResult errors,
            @ModelAttribute("filterForm") final FilterForm filterForm) {
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("medico");
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", List.of(WeekdayEnum.values()));
            mav.addObject("hoursSelectItems", getHoursSelectItems());
            mav.addObject("searchForm", new SearchForm());
            return mav;
        }

        // Si no hay errores, proceder con la creación del médico
        User doc = us.createDoctor(form.getEmail(), "12345678", form.getName() + " " + form.getSurname(), "med-licence", form.getSpeciality()); //TODO magicnumber password sacar y getLicence
        dcs.addCoverages(doc.getId(), form.getObrasSociales());
        dss.createShifts(doc.getId(), form.getSchedules().getWeekday(), form.getAddress(), LocalTime.parse(form.getSchedules().getStartTime()), LocalTime.parse(form.getSchedules().getEndTime()), form.getAmount());//TODO change Schedule model or sth.
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("docList", dds.getAllDoctors());
        mav.addObject("searchForm", new SearchForm());
        return mav;
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
