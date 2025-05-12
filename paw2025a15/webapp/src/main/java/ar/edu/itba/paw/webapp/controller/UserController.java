package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.webapp.controller.Util.SelectItem;
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

import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.form.ProfileForm;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService us;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private FileService fs;

    @Autowired
    private InsuranceService is;

    @Autowired
    private DoctorDetailService dds;

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
            return profile(user, profileForm, result, locale);
        }

        long pictureId;
        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {
            File f = fs.create(profileForm.getProfileImage().getBytes(), FileTypeEnum.fromString(profileForm.getProfileImage().getContentType()));
            pictureId = f.getId();
            LOGGER.info("Updating profile picture for user with id: {}", user.getId());
        }else {
            pictureId = user.getPictureId();
        }
        us.editUser(user.getId(), user.getName(),profileForm.getPhoneNumber(), pictureId);
        us.updateLocale(user.getId(), profileForm.getMailLanguage());
        
        if(user.getRole().equals(UserRoleEnum.PATIENT)) {
            pds.updatePatientDetails(user.getId(), profileForm.getBirthDate(), profileForm.getBloodType(), profileForm.getHeight(), profileForm.getWeight(), profileForm.getSmokes(), profileForm.getDrinks(), profileForm.getMeds(), profileForm.getConditions(), profileForm.getAllergies(), profileForm.getDiet(), profileForm.getHobbies(), profileForm.getJob() );
        } else {
            dds.updateDoctorCoverages(user.getId(), profileForm.getInsurances());
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
        ModelAndView mav = new ModelAndView("profileInfo");

        if (profileForm.getPhoneNumber() == null) {
            profileForm.setPhoneNumber(user.getTelephone());
        }
        if (profileForm.getMailLanguage() == null) {
            profileForm.setMailLanguage(user.getLocale());
        }

        if(user.getRole().equals(UserRoleEnum.PATIENT)) {
            mav.addObject("patientDetails", pds.getDetailByPatientId(user.getId()).orElseThrow(() -> new NotFoundException("Patient details not found for user with id: " + user.getId())));
        } else if (user.getRole().equals(UserRoleEnum.DOCTOR)) {
            mav.addObject("patientDetails", null);
            profileForm.setInsurances(InsuranceToLong(dds.getDoctorInsurancesById(user.getId())));
            DoctorDetail doctorDetail = dds.getDetailByDoctorId(user.getId()).orElseThrow(() -> new NotFoundException("Doctor details not found for user with id: " + user.getId()));
            mav.addObject("doctorDetail", doctorDetail);
        }

        mav.addObject("obrasSocialesItems", is.getAllInsurances());
        mav.addObject("bloodTypes", BloodTypeEnum.values());
        mav.addObject("locales", SelectItem.getLocalesSelectItems(messageSource , locale));
        mav.addObject("landingForm", new LandingForm());

        return mav;
    }

    static List<Long> InsuranceToLong(List<Insurance> insurances) {
        List<Long> insurancesLong = new ArrayList<>();
        for(Insurance insurance : insurances) {
            insurancesLong.add(insurance.getId());
        }
        return insurancesLong;
    }
}