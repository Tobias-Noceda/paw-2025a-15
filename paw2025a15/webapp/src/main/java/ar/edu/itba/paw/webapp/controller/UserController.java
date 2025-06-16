package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.controller.util.SelectItem;
import ar.edu.itba.paw.webapp.form.LandingForm;
import ar.edu.itba.paw.webapp.form.ProfileForm;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PatientService ps;

    @Autowired
    private FileService fs;

    @Autowired
    private InsuranceService is;

    @Autowired
    private DoctorService ds;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(path = "/profile", method = RequestMethod.POST)
    public ModelAndView saveProfileInfo(
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
        BindingResult result,
        RedirectAttributes redirectAttrs,
        Locale locale
    ) throws IOException {
        if (result.hasErrors()) {
            System.out.println(result.toString());
            return profile(user, profileForm, result, locale);
        }

        File picture;
        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {
            File f = fs.create(profileForm.getProfileImage().getBytes(), FileTypeEnum.fromString(profileForm.getProfileImage().getContentType()));
            picture = f;
            LOGGER.info("Updating profile picture for user with id: {}", user.getId());
        }else {
            picture = user.getPicture();
        }
        
        if(user instanceof Doctor doctor) {
            ds.updateDoctor(doctor, profileForm.getPhoneNumber(), picture, profileForm.getMailLanguage(), profileForm.getInsurances());
            ds.updateShifts(doctor.getId(), profileForm.getSchedules().getWeekday(), profileForm.getAddress(), LocalTime.parse(profileForm.getSchedules().getStartTime()), LocalTime.parse(profileForm.getSchedules().getEndTime()), profileForm.getAmount(), false);
        } else {
            ps.updatePatient(
                (Patient) user,
                profileForm.getPhoneNumber(),
                picture,
                profileForm.getMailLanguage(),
                profileForm.getBirthDate(),
                profileForm.getBloodType(),
                profileForm.getHeight() != null ? BigDecimal.valueOf(profileForm.getHeight()) : null,
                profileForm.getWeight() != null ? BigDecimal.valueOf(profileForm.getWeight()) : null,
                profileForm.getSmokes(),
                profileForm.getDrinks(),
                profileForm.getMeds(),
                profileForm.getConditions(),
                profileForm.getAllergies(),
                profileForm.getDiet(),
                profileForm.getHobbies(),
                profileForm.getJob(),
                null,
                null
            );
        }
        LOGGER.info("User with id: {} updated!", user.getId());
        
        redirectAttrs.addFlashAttribute("updateSuccessMessage","✅ Your profile has been updated!");

        return new ModelAndView("redirect:/profile");
    }
    
    @RequestMapping("/profile")
    public ModelAndView profile(
        @ModelAttribute("user_data") User user,
        @ModelAttribute("profileForm") ProfileForm profileForm,
        BindingResult result,
        Locale locale
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        
        ModelAndView mav = new ModelAndView("profileInfo");

        if (profileForm.getPhoneNumber() == null) {
            profileForm.setPhoneNumber(user.getTelephone());
        }
        if (profileForm.getMailLanguage() == null) {
            profileForm.setMailLanguage(user.getLocale());
        }

        if (user instanceof Doctor doctor) {
            profileForm.setInsurances(
                doctor.getInsurances() != null ? insuranceToLong(doctor.getInsurances()) : new ArrayList<>()
            );
            profileForm.setSchedules(doctor.getSchedules());
            profileForm.setAmount(doctor.getSingleShifts().getFirst().getDuration());
            List<String> selectedDays = doctor.getSchedules().getWeekday()
                    .stream()
                    .map(Enum::name)
                    .toList();
            mav.addObject("selectedDays", selectedDays);
            profileForm.setAddress(doctor.getSchedules().getAddress());
            mav.addObject("doctorDetail", doctor);
        } else {
            mav.addObject("patientDetails", ps.getPatientById(user.getId()).orElse(null));
        }

        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("weekdaySelectItems", SelectItem.getListOfWeekdays(messageSource, locale));
        mav.addObject("hoursSelectItems", SelectItem.getHoursSelectItems());
        mav.addObject("bloodTypes", BloodTypeEnum.values());
        mav.addObject("locales", SelectItem.getLocalesSelectItems(messageSource , locale));
        mav.addObject("landingForm", new LandingForm());
        return mav;
    }

    static List<Long> insuranceToLong(List<Insurance> insurances) {
        List<Long> insurancesLong = new ArrayList<>();
        for(Insurance insurance : insurances) {
            insurancesLong.add(insurance.getId());
        }
        return insurancesLong;
    }
}