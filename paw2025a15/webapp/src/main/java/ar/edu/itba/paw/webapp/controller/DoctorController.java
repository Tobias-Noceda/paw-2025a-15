package ar.edu.itba.paw.webapp.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
import ar.edu.itba.paw.form.TakeTurnForm;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
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

    private List<SelectItem> getWeekdaySelectItems() {
        final List<SelectItem> items =  new ArrayList<>();
        for(WeekdayEnum weekday: WeekdayEnum.values()){
            items.add(new SelectItem(weekday.getName(), weekday.getName()));
        }

        return items;
    }

    private List<SelectItem> getObrasSociales() {
        List<SelectItem> obrasSociales = List.of(
                new SelectItem("1","OSDE"),
                new SelectItem("2","Swiss Medical"),
                new SelectItem("3","Galeno")
        );
        return obrasSociales;
    }

    private List<SelectItem> getHoursSelectItems() {
        final List<SelectItem> times = new ArrayList<>();
        for(Integer hour = 6;hour < 22;hour++){
            StringBuilder a = new StringBuilder().append(hour).append(":00");
            StringBuilder b = new StringBuilder().append(hour).append(":30");
            times.add(new SelectItem(a.toString(),a.toString()));
            times.add(new SelectItem(b.toString(),b.toString()));
        }
        return times;
    }


    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView doctorProfile(@PathVariable("id") long id, @ModelAttribute("takeTurnForm") final TakeTurnForm form) {
        final ModelAndView mav = new ModelAndView("doctor-detail");
        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances" ,dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getShiftsByDoctorId(id));
        mav.addObject("doctorAppointments", dss.getAvailableTurnsByDoctorIdByMonth(id, LocalDate.now().getMonth()));
        mav.addObject("doctorId", id);

        return mav;
    }

    @RequestMapping(value = "/doctors/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView takeTurn(@PathVariable("id") long id, @Valid @ModelAttribute("takeTurnForm") final TakeTurnForm form, final BindingResult errors) {
        final ModelAndView mav = new ModelAndView("redirect:/");
        if (errors.hasErrors()) {
            return doctorProfile(id, form);
        }

        User patient = us.create(form.getEmail(), "12345678", form.getName() + " " + form.getSurname());
        as.addApointment(form.getShiftId(), patient.getId(), LocalDate.parse(form.getDate()));
        // TODO: send email to doctor with appointment details

        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", getObrasSociales());//TODO vincularlo con la tabla de Insurance
        mav.addObject("weekdaySelectItems", getWeekdaySelectItems());
        mav.addObject("hoursSelectItems", getHoursSelectItems());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        User doc = us.createDoctor(form.getEmail(), "12345678", form.getName() + " " + form.getSurname(), "med-licence", form.getSpeciality()); //TODO magicnumber password sacar y getLicence
        //TODO OJO! puede tener más de una specialty no?
        //dcs.addCoverages(doc.getId(), form.getInsurances());TODO
        dss.createShifts(doc.getId(), form.getSchedules().getWeekday(), form.getSchedules().getAddress(), LocalTime.parse(form.getSchedules().getStartTime()), LocalTime.parse(form.getSchedules().getEndTime()), form.getSchedules().getShiftCount());
        final ModelAndView mav = new ModelAndView("index");
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
