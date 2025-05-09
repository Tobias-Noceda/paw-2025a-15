package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import ar.edu.itba.paw.interfaces.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.form.LandingForm;
import ar.edu.itba.paw.form.ProfileForm;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.exceptions.MediaTypeException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    private FileService fs;

    @Autowired
    private UserService us;

    @Autowired
    private InsuranceService is;

    @Autowired
    private StudyService ss;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @GetMapping("/favicon.ico")
    public ResponseEntity<byte[]> getFavicon() throws IOException {
        String path = servletContext.getRealPath("/resources/favicon.png");
        java.io.File imgFile = new java.io.File(path);

        byte[] bytes = Files.readAllBytes(imgFile.toPath());
        
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }

    @RequestMapping(path = "/supersecret/files/logo", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getIconForm() throws IOException {
        String path = servletContext.getRealPath("/resources/icono.jpg");
        java.io.File imgFile = new java.io.File(path);
    
        byte[] bytes = Files.readAllBytes(imgFile.toPath());
    
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    @RequestMapping(path = "/supersecret/user-profile-pic/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getUserPicture(@PathVariable("id") long id){
        File f = us.getUserPicture(id).orElseThrow(() -> new NoSuchElementException("Profile picture not found for user with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();
        
        MediaType mediaType;
        mediaType = switch (fileType) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPEG -> MediaType.IMAGE_JPEG;
            default -> throw new MediaTypeException("The profile picture must be an image type of file");//TODO logging y constraints al modificar y crear imagenes de perfil de usuario
        };

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(content);
    }

    @RequestMapping(path = "/supersecret/insurance-picture/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getInsurancePicture(@PathVariable("id") long id){
        File f = is.getInsurancePicture(id).orElseThrow(() ->  new NoSuchElementException("Logo picture not found for insurance with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();
        
        MediaType mediaType;
        mediaType = switch (fileType) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPEG -> MediaType.IMAGE_JPEG;
            default -> throw new MediaTypeException("The logo picture must be an image type of file");//TODO logging y constraints al modificar y crear imagenes de logo de insurance
        };

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(content);
    }

    @RequestMapping(method=RequestMethod.GET, path="/view-study/{id:\\d+}")//TODO necesita re filtrado por roles y permisos en auth esto
    public @ResponseBody ResponseEntity<byte[]> getStudy(@PathVariable("id") long id){
        File f = ss.getStudyFile(id).orElseThrow(() -> new NoSuchElementException("Study not found with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();
        
        MediaType mediaType;
        mediaType = switch (fileType) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPEG -> MediaType.IMAGE_JPEG;
            case PDF -> MediaType.APPLICATION_PDF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"file_" + id + getExtension(fileType) + "\"")
                .body(content);
    }


    @RequestMapping(path = "/save-profile", method = RequestMethod.POST)
    public ModelAndView saveProfileInfo(@AuthenticationPrincipal UserDetails userDetails,
                    @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                    BindingResult result,
                                        RedirectAttributes redirectAttrs
    ) throws IOException {
        User user = us.getCurrentUser();
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("profileInfo");

            mav.addObject("profileForm", profileForm);
            mav.addObject("bloodTypes", BloodTypeEnum.values());
            mav.addObject("user", user);
            mav.addObject("landingForm", new LandingForm());
            
            if(user.getRole().equals(UserRoleEnum.PATIENT)) {
                mav.addObject("patientDetails", pds.getDetailByPatientId(user.getId()).orElse(null));
            } else {
                mav.addObject("patientDetails", null);
            }

            return mav;
        }

        if (profileForm.getProfileImage() != null && !profileForm.getProfileImage().isEmpty()) {
            File f = fs.create(profileForm.getProfileImage().getBytes(), FileTypeEnum.fromString(profileForm.getProfileImage().getContentType()));
            us.editUser(user.getId(), user.getName(),profileForm.getPhoneNumber(),f.getId());
        }else {
            us.editUser(user.getId(), user.getName(),profileForm.getPhoneNumber(),user.getPictureId());
        }
        if(user.getRole().equals(UserRoleEnum.PATIENT)) {
            //pds.updatePatientDetails(user.getId(), LocalDate.of(LocalDate.now().getYear() - profileForm.getAge(), 1, 1), profileForm.getBloodType(), profileForm.getHeight(), profileForm.getWeight(), profileForm.getSmokes(), profileForm.getDrinks(), profileForm.getMeds(), profileForm.getConditions(), profileForm.getAllergies(), profileForm.getDiet(), profileForm.getHobbies(), profileForm.getJob());
            pds.updatePatientDetails(user.getId(), profileForm.getBirthDate(), profileForm.getBloodType(), profileForm.getHeight(), profileForm.getWeight(), profileForm.getSmokes(), profileForm.getDrinks(), profileForm.getMeds(), profileForm.getConditions(), profileForm.getAllergies(), profileForm.getDiet(), profileForm.getHobbies(), profileForm.getJob() );
        } else {
            dds.createDoctorCoverages(user.getId(), profileForm.getInsurances());
        }
        redirectAttrs.addFlashAttribute("updateSuccessMessage",

                "✅ Your profile has been updated!");

        return new ModelAndView("redirect:/profile");
    }

    private String getExtension(FileTypeEnum mediaType) {
        return switch (mediaType) {
            case PNG -> ".png";
            case JPEG -> ".jpg";
            case PDF -> ".pdf";
            default -> "";
        };
    }

    // TODO: Delete
    @RequestMapping(path = "/supersecret/file", method = RequestMethod.GET)
    public ModelAndView getImageForm(){
        return new ModelAndView("createFile");
    }

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.POST)
    public ModelAndView createImage(@RequestParam("file") MultipartFile file) throws IOException{
        File f = fs.create(file.getBytes(), FileTypeEnum.fromString(file.getContentType()));
        return new ModelAndView("redirect:/supersecret/files/" + f.getId());
    }
}
