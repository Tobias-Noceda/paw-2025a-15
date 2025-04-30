package ar.edu.itba.paw.webapp.controller;

import java.time.LocalTime;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.LabForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.interfaces.services.DoctorCoverageService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;

@Controller
public class RegisterController {

    @Autowired
    private UserService us;

    @Autowired
    private InsuranceService is;

    @Autowired
    private DoctorCoverageService dcs;
    
    @Autowired
    private DoctorShiftService dss;
    
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private void loginUser(String email, String password) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @RequestMapping(value = "/createPatient", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerPatientForm") final PatientForm form,
            final BindingResult errors
    ) {
        boolean isValid;

        if (errors.hasErrors()) {
            isValid = false;
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.passwordMismatch");
            isValid = false;
        } else if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists");
            isValid = false;
        } else {
            try {
                us.create(
                        form.getEmail(),
                        passwordEncoder.encode(form.getPassword()),
                        form.getName() + " " + form.getSurname(),
                        form.getPhoneNumber(),
                        UserRoleEnum.PATIENT
                );
                loginUser(form.getEmail(), form.getPassword());
                return new ModelAndView("redirect:/");
            } catch (Exception e) {
                errors.reject("error.registerPatientFailed");
                isValid = false;
            }
        }

        if (!isValid) {
            final ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("registerPatientForm", form);
            // Agregar los atributos usados por el formulario de médicos
            mav.addObject("registerMedicForm", new DoctorForm()); // si Spring necesita esto
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, Locale.getDefault()));
            mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, Locale.getDefault()));
            mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
            return mav;
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    @RequestMapping(value = "/createMedic", method = RequestMethod.POST)
    public ModelAndView registerMedic(
            @Valid @ModelAttribute("registerMedicForm") final DoctorForm form,
            final BindingResult errors,
            Locale locale
    ) {
        boolean isValid = true;

        if (errors.hasErrors()) {
            isValid = false;
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.passwordMismatch");
            isValid = false;
        } else if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists");
            isValid = false;
        } else {
            try {
                // Crear el médico
                User doc = us.createDoctor(
                        form.getEmail(),
                        passwordEncoder.encode(form.getPassword()),
                        form.getName() + " " + form.getSurname(),
                        form.getPhoneNumber(),
                        "med-licence",
                        form.getSpeciality()
                );
                dcs.addCoverages(doc.getId(), form.getObrasSociales());
                dss.createShifts(
                        doc.getId(),
                        form.getSchedules().getWeekday(),
                        form.getAddress(),
                        LocalTime.parse(form.getSchedules().getStartTime()),
                        LocalTime.parse(form.getSchedules().getEndTime()),
                        form.getAmount()
                );
                loginUser(form.getEmail(), form.getPassword());
            } catch (Exception e) {
                errors.reject("error.registerDoctorFailed");
                isValid = false;
            }
        }

        if (isValid) {
            return new ModelAndView("redirect:/home");
        } else {
            final ModelAndView mav = new ModelAndView("doctorForm");
            mav.addObject("registerMedicForm", form);
            mav.addObject("registerPatientForm", new PatientForm());
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
            mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
            mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
            return mav;
        }
    }


    @RequestMapping(value = "/createLab", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerLabForm") final LabForm form,
            final BindingResult errors
    ) {
        boolean isValid = true;

        if(errors.hasErrors()) {
            isValid = false;
        } else if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.passwordMismatch");
            isValid = false;
        } else if (us.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.emailExists");
            isValid = false;
        } else {
            try {
                // TODO: Crear el laboratorio
            } catch (Exception e) {
                errors.reject("error.registerLaboratoryFailed");
                isValid = false;
            }
        }

        if(!isValid) {
            final ModelAndView mav = new ModelAndView("labForm");
            mav.addObject("lab", form);
            return mav;
        } else {
            return new ModelAndView("redirect:/");
        }

    }
}
