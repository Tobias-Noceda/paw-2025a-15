package ar.edu.itba.paw.webapp.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.DoctorForm;
import ar.edu.itba.paw.form.PatientForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;

@Controller
public class RegisterController {

    @Autowired
    private UserService us;

    @Autowired
    private InsuranceService is;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private DoctorShiftService dss;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping("/register")
    public ModelAndView medico(@ModelAttribute("registerMedicForm") final DoctorForm form, Locale locale, @ModelAttribute("registerPatientForm") final PatientForm patientForm) {
        final ModelAndView mav = new ModelAndView("registryForm");
        mav.addObject("doctor", form);
        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
        mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
        return mav;
    }

    @RequestMapping(value = "/createPatient", method = RequestMethod.POST)
    public ModelAndView registerForm(
            @Valid @ModelAttribute("registerPatientForm") final PatientForm form,
            final BindingResult errors
    ) {
        boolean isValid;

        if (errors.hasErrors()) {
            isValid = false;
        } else {
            try {
                User newUser = pds.createPatient(
                        form.getEmail(),
                        form.getPassword(),
                        form.getName() + " " + form.getSurname(),
                        form.getPhoneNumber(),
                        LocaleEnum.fromLocale(LocaleContextHolder.getLocale())
                );
                pds.updatePatientDetails(newUser.getId(), form.getBirthDate(),null, form.getHeight(), form.getWeight(), null, null , null, null, null, null, null, null);
                loginUser(form.getEmail(), form.getPassword());
                return new ModelAndView("redirect:/");
            } catch (Exception e) {
                errors.reject("error.registerPatientFailed");
                isValid = false;
            }
        }

        if (!isValid) {
            final ModelAndView mav = new ModelAndView("registryForm");
            mav.addObject("registerPatientForm", form);
            mav.addObject("registerMedicForm", new DoctorForm());
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
        } else {
            try {
                User doc = dds.createDoctor(
                        form.getEmail(),
                        form.getPassword(),
                        form.getName() + " " + form.getSurname(),
                        form.getPhoneNumber(),
                        form.getDoctorLicense(),
                        form.getSpecialty(),
                        LocaleEnum.fromLocale(LocaleContextHolder.getLocale())
                );
                if(form.getObrasSociales()!=null) {
                    dds.createDoctorCoverages(doc.getId(), form.getObrasSociales());
                }
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
            final ModelAndView mav = new ModelAndView("registryForm");
            mav.addObject("registerMedicForm", form);
            mav.addObject("registerPatientForm", new PatientForm());
            mav.addObject("obrasSocialesItems", is.getAllInsurances());
            mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
            mav.addObject("specialtySelectItems", SelectItem.getListOfSpecialties(messageSource, locale));
            mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
            return mav;
        }
    }

    private static boolean isRangeValid(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);

        return start.isBefore(end);
    }

    private void loginUser(String email, String password) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}