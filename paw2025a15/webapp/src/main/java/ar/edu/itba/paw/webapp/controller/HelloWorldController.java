package ar.edu.itba.paw.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import ar.edu.itba.paw.models.WeekdayEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.EmailService;

@Controller
public class HelloWorldController {

    @Autowired
    private DoctorService ds;

    @Autowired
    private EmailService es;


    private EmailService es;

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

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/estudios")
    public ModelAndView estudios() {
        final ModelAndView mav = new ModelAndView("estudios");
        return mav;
    }

    @RequestMapping("/obras-sociales")
    public ModelAndView obrasSociales() {
        final ModelAndView mav = new ModelAndView("obras-sociales");
        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", getObrasSociales());
        mav.addObject("weekdaySelectItems", getWeekdaySelectItems());
        mav.addObject("hoursSelectItems", getHoursSelectItems());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        ds.create(form.getName(), form.getEmail(), form.getObrasSociales(),form.getSpecialty(), form.getSchedules());
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value = "/register", method=RequestMethod.GET)
    public ModelAndView registerForm() {
        return new ModelAndView("register");
    }

    @RequestMapping(value = "/testMail")
    public ModelAndView testMail(){
        es.sendTestEmail();
        return new ModelAndView("redirect:/");
    }

}