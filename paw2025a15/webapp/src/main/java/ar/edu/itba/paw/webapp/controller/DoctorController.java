package ar.edu.itba.paw.webapp.controller;

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
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;

@Controller
public class DoctorController {

    private final UserService us;
    private final DoctorDetailService dds;
    private final DoctorCoverageService dcs;
    private final DoctorShiftService dss;

    @Autowired
    public DoctorController(final UserService us, final DoctorDetailService dds, final DoctorCoverageService dcs, final DoctorShiftService dss){
        this.us = us;
        this.dds = dds;
        this.dcs = dcs;
        this.dss = dss;
    }

    @RequestMapping("/doctors/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("doctor-detail");
        dds.getDetailByDoctorId(id).ifPresent(doctorDetail -> mav.addObject("doctorDetail", doctorDetail));//TODO throw exception if not doctor
        us.getUserById(id).ifPresent(doctor -> mav.addObject("doctor", doctor));
        mav.addObject("doctorInsurances" ,dcs.getInsurancesById(id));
        mav.addObject("doctorShifts", dss.getShiftsByDoctorId(id));
        return mav;
    }

    @RequestMapping("/medico")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form) {
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("doctor", form);
        mav.addObject("items", getObrasSociales());
        return mav;
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerForm(@Valid @ModelAttribute("registerMedicForm") final DoctorForm form, final BindingResult errors) {
        //us.createMedic(form.getName(), form.getEmail());
        final ModelAndView mav = new ModelAndView("medico");
        mav.addObject("form", form);
        return mav;
    }

    private List<SelectItem> getObrasSociales() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("OS1", "Obra Social 1"));
        items.add(new SelectItem("OS2", "Obra Social 2"));
        items.add(new SelectItem("OS3", "Obra Social 3"));
        // Añadir más obras sociales aquí

        return items;
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
