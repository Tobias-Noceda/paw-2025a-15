package ar.edu.itba.paw.webapp.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.ChangePasswordForm;
import ar.edu.itba.paw.form.RecoverForm;
import ar.edu.itba.paw.interfaces.services.UserService;

@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService us;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView mav = new ModelAndView("login");
        if (error != null) {
            switch (error) {
                case "userNotFound" -> mav.addObject("errorMessage", "error.invalidCredentials");
                case "invalidCredentials" -> mav.addObject("errorMessage", "error.invalidCredentials");
                case "credentialsNotFound" -> mav.addObject("errorMessage", "error.credentialsNotFound");
                default -> mav.addObject("errorMessage", "error.invalidCredentials");
            }
        }
        return mav;
    }

    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPassword(@ModelAttribute("recoverPass") RecoverForm form) {
        ModelAndView mav = new ModelAndView("forgotPassword");
        return mav;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.GET)
    public ModelAndView showRecoverPasswordPage() {
        ModelAndView mav = new ModelAndView("forgotPassword");
        mav.addObject("successMessage", "checkEmail");
        return mav;
    }

    @RequestMapping(value = "/recover-password", method = RequestMethod.POST)
    public ModelAndView recoverPassword(
            @Valid @ModelAttribute("recoverPass") RecoverForm form,
            BindingResult result
    ) {
        LOGGER.debug("Processing password recovery for email: {}", form.getEmail());
        ModelAndView mav;

        // Si hay errores de validación, volver a la página forgotPassword
        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found for email: {}", form.getEmail());
            return new ModelAndView("forgotPassword");
        }

        try {
            us.askPasswordRecover(form.getEmail());
            LOGGER.info("Password reset email sent to: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("successMessage", "linkSent");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("No user found with email: {}", form.getEmail());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "emailNotFound");
            mav.addObject("recoverPass", form);
        } catch (Exception e) {
            LOGGER.error("Error sending password reset email: {}", e.getMessage());
            mav = new ModelAndView("forgotPassword");
            mav.addObject("errorMessage", "processingError");
            mav.addObject("recoverPass", form);
        }

        return mav;
    }

    @RequestMapping(value = "/change-password/{token}/{id}", method = RequestMethod.GET)
    public ModelAndView changePassword(
        @PathVariable("id") long id, @PathVariable("token") String token ,
        @ModelAttribute("passwordForm") ChangePasswordForm form
    ){
        ModelAndView mav = new ModelAndView("changePassword");

        return mav;
    }

    @RequestMapping(value = "/change-password/{token}/{id}", method = RequestMethod.POST)
    public ModelAndView changePassword(
        @ModelAttribute("passwordForm") final ChangePasswordForm form,
        @PathVariable("id") long id, @PathVariable("token") String token
    ){
        if(!form.getPassword().equals(form.getRepeatPassword())){
            //volver a pedirle al usuario
            ModelAndView mav = new ModelAndView("changePassword");
            mav.addObject("errorMessage", "Las contraseñas no coinciden");
            return mav;
        }
        ModelAndView mav = new ModelAndView("login");
        us.changePasswordByID(id, form.getPassword());
        LOGGER.info("Password changed for user with id: {}", id);

        return mav;
    }
}