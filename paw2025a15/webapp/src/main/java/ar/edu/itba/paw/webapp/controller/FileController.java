package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import ar.edu.itba.paw.form.ProfileForm;
import ar.edu.itba.paw.form.SearchForm;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.enums.BloodTypeEnum;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;

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

    //===================================TODO: new, needs mappind in auth=================================================

    @RequestMapping(path = "/getUserPic/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getUserPicture(@PathVariable("id") long id){
        Optional<File> f = us.getUserPicture(id);
        if (!f.isPresent()) {
            throw new NoSuchElementException("Profile picture not found for user with ID: " + id);
        }
        
        byte[] content = f.get().getContent();
        FileTypeEnum fileType = f.get().getType();
        
        MediaType mediaType;
        mediaType = switch (fileType) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPEG -> MediaType.IMAGE_JPEG;
            default -> throw new IllegalArgumentException("The profile picture must be an image type of file");//TODO logging y constraints al modificar y crear imagenes de perfil de usuario
        };

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(content);
    }

    @RequestMapping(path = "/getInsurancePic/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getInsurancePicture(@PathVariable("id") long id){
        Optional<File> f = is.getInsurancePicture(id);
        if (!f.isPresent()) {
            throw new NoSuchElementException("Logo picture not found for insurance with ID: " + id);
        }
        
        byte[] content = f.get().getContent();
        FileTypeEnum fileType = f.get().getType();
        
        MediaType mediaType;
        mediaType = switch (fileType) {
            case PNG -> MediaType.IMAGE_PNG;
            case JPEG -> MediaType.IMAGE_JPEG;
            default -> throw new IllegalArgumentException("The logo picture must be an image type of file");//TODO logging y constraints al modificar y crear imagenes de logo de insurance
        };

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(content);
    }

    @RequestMapping(method=RequestMethod.GET, path="/getStudy/{id:\\d+}")//TODO necesita re filtrado por roles y permisos en auth esto
    public @ResponseBody ResponseEntity<byte[]> getStudy(@PathVariable("id") long id){
        Optional<File> f = ss.getStudyFile(id);
        if (!f.isPresent()) {
            throw new NoSuchElementException("Study not found with ID: " + id);
        }
        
        byte[] content = f.get().getContent();
        FileTypeEnum fileType = f.get().getType();
        
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
                .body(content);
    }

    //====================================================================================================================

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.GET)
    public ModelAndView getImageForm(){
        return new ModelAndView("createFile");
    }

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.POST)
    public ModelAndView createImage(@RequestParam("file") MultipartFile file) throws IOException{
        File f = fs.create(file.getBytes(), FileTypeEnum.fromString(file.getContentType()));
        return new ModelAndView("redirect:/supersecret/files/" + f.getId());
    }


    @RequestMapping(path = "/save-profile", method = RequestMethod.POST)
    public ModelAndView saveProfileInfo(@AuthenticationPrincipal UserDetails userDetails,
                                  @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                  BindingResult result,
                                  @ModelAttribute("searchForm") final SearchForm searchForm
    ) throws IOException {
        User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("No such email"));
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("profileInfo");

            mav.addObject("profileForm", profileForm);
            mav.addObject("bloodTypes", BloodTypeEnum.values());
            mav.addObject("user", user);

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
        pds.updatePatientDetails(user.getId(), profileForm.getAge(), profileForm.getBloodType(), profileForm.getHeight(), profileForm.getWeight(), profileForm.getSmokes(), profileForm.getDrinks(), profileForm.getMeds(), profileForm.getConditions(), profileForm.getAllergies(), profileForm.getDiet(), profileForm.getHobbies(), profileForm.getJob());
        return new ModelAndView("redirect:/profile");
    }



    @RequestMapping(method=RequestMethod.GET, path="/supersecret/files/{file_id:\\d+}")//TODO necesita re filtrado por roles y permisos en auth esto
    public @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable("file_id") long id){
        Optional<File> f = fs.findById(id);
        if (!f.isPresent()) {
            throw new NoSuchElementException("File not found with ID: " + id);
        }
        
        byte[] content = f.get().getContent();
        FileTypeEnum fileType = f.get().getType();
        
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
                .body(content);
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
}
