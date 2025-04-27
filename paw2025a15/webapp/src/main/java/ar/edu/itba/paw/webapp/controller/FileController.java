package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import ar.edu.itba.paw.form.ProfileForm;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.FileTypeEnum;

@Controller
public class FileController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    private FileService fs;

    @Autowired
    private UserService us;

    @Autowired
    private PatientDetailService pds;

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
    public ModelAndView saveImage(@AuthenticationPrincipal UserDetails userDetails, @Valid @ModelAttribute("profileForm") ProfileForm profileForm
    ) throws IOException {
        User user = us.getUserByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("No such email"));
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
