package ar.edu.itba.paw.webapp.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;
import ar.edu.itba.paw.webapp.form.InsuranceForm;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

@Controller
class AdminController {
    
    @Autowired
    private InsuranceService is;

    @Autowired
    private FileService fs;

    private final static int PAGE_SIZE = 6; // 3x2 grid de cards


    @RequestMapping("/admin/home")
    public ModelAndView listInsurances(
        @ModelAttribute("user_data") User user,
        @RequestParam(defaultValue = "1") int page
    ) { 
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        List<Insurance> insurancesPage = is.getInsurancesPage(page, PAGE_SIZE);
        int totalInsurances = is.getInsurancesCount();
        int totalPages = (int) Math.ceil((double) totalInsurances / PAGE_SIZE);
        
        ModelAndView mav = new ModelAndView("admin/index");
        mav.addObject("insurances", insurancesPage);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", totalPages);

        return mav;
    }

    @RequestMapping("/admin/insurances/new")
    public ModelAndView newInsuranceForm(
        @ModelAttribute("user_data") User user,
        @ModelAttribute("insuranceForm") InsuranceForm insuranceForm
    ) {
        if(user == null) {
            throw new UnauthorizedException("User not found");
        }
        
        return new ModelAndView("admin/insuranceForm");

    }

    @RequestMapping(value = "/admin/insurances/new", method = RequestMethod.POST)
    public ModelAndView createInsurance(
        @ModelAttribute("user_data") User user,
        @Valid @ModelAttribute("insuranceForm") InsuranceForm insuranceForm,
        RedirectAttributes redirectAttrs
    ) {
        try {
            File pictureFile;
            if (insuranceForm.getPicture() != null && !insuranceForm.getPicture().isEmpty()) {
                pictureFile = fs.create(insuranceForm.getPicture().getBytes(), FileTypeEnum.fromString(insuranceForm.getPicture().getContentType()));
            } else {
                // Usar imagen por defecto si no se proporciona
                pictureFile = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found"));
            }

            is.create(insuranceForm.getName(), pictureFile);
            redirectAttrs.addFlashAttribute("successMessage", "created");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "created");
        }

        return new ModelAndView("redirect:/admin/home");
    }

    @RequestMapping("/admin/insurances/edit/{id}")
    public ModelAndView editInsuranceForm(
        @ModelAttribute("user_data") User user,
        @PathVariable("id") long id,
        @ModelAttribute("insuranceForm") InsuranceForm insuranceForm
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        Insurance insurance = is.getInsuranceById(id)
                .orElseThrow(() -> new NotFoundException("Insurance not found"));


        ModelAndView mav = new ModelAndView("admin/insuranceForm");
        mav.addObject("insurance", insurance);
        mav.addObject("isEdit", true);

        return mav;
    }

    @RequestMapping(value = "/admin/insurances/edit/{id}", method = RequestMethod.POST)
    public ModelAndView updateInsurance(
        @ModelAttribute("user_data") User user,
        @PathVariable("id") long id,
        @Valid @ModelAttribute("insuranceForm") InsuranceForm insuranceForm,
        BindingResult errors,
        RedirectAttributes redirectAttrs
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }
        Insurance insurance = is.getInsuranceById(id)
                .orElseThrow(() -> new NotFoundException("Insurance not found"));

        try {
            File pictureFile;
            if (insuranceForm.getPicture() != null && !insuranceForm.getPicture().isEmpty()) {
                pictureFile = fs.create(insuranceForm.getPicture().getBytes(), FileTypeEnum.fromString(insuranceForm.getPicture().getContentType()));
            } else {
                pictureFile = insurance.getPicture(); // Mantener la imagen actual
            }

            is.edit(id, insuranceForm.getName(), pictureFile);
            redirectAttrs.addFlashAttribute("successMessage", "updated");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", "updated");
        }

        return new ModelAndView("redirect:/admin/home");
    }

    @RequestMapping(value = "/admin/insurances/delete/{id}", method = RequestMethod.POST)
    public ModelAndView deleteInsurance(
        @ModelAttribute("user_data") User user,
        @PathVariable("id") long id,
        RedirectAttributes redirectAttrs
    ) {
        if (user == null) {
            throw new UnauthorizedException("User not found");
        }

        try {
            is.delete(id);
            redirectAttrs.addFlashAttribute("successMessage", "deleted");
            
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw e; // Propagar NotFoundException para manejarla en el controlador global
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "deleted");
            }
        }

        return new ModelAndView("redirect:/admin/home");
    }
}