package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

@Controller
public class FileController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    private UserService us;

    @Autowired
    private InsuranceService is;

    @Autowired
    private StudyService ss;

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

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .body(content);
    }

    @RequestMapping(path = "/supersecret/insurance-picture/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getInsurancePicture(@PathVariable("id") long id){
        File f = is.getInsurancePicture(id).orElseThrow(() ->  new NoSuchElementException("Logo picture not found for insurance with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .body(content);
    }

    @RequestMapping(method=RequestMethod.GET, path="/view-study/{id:\\d+}")
    public @ResponseBody ResponseEntity<byte[]> getStudy(@PathVariable("id") long id){
        File f = ss.getStudyFile(id).orElseThrow(() -> new NoSuchElementException("Study not found with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"file_" + id + fileType.getExtension() + "\"")
                .body(content);
    }
}
