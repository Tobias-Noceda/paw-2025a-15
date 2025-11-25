package ar.edu.itba.paw.webapp.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

@Controller
public class FileController {

    @Autowired
    private UserService us;

    @Autowired
    private InsuranceService is;

    @Autowired
    private FileService fs;

    @RequestMapping(path = "/supersecret/user-profile-pic/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getUserPicture(@PathVariable("id") long id){
        User user = us.getUserById(id).orElseThrow(() -> new NoSuchElementException("Profile picture not found for user with ID: " + id));
        File f = user.getPicture();
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .body(content);
    }

    @RequestMapping(path = "/supersecret/insurance-picture/{id:\\d+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<byte[]> getInsurancePicture(@PathVariable("id") long id){
        Insurance insurance = is.getInsuranceById(id).orElseThrow(() -> new NoSuchElementException("Logo picture not found for insurance with ID: " + id));
        File f = insurance.getPicture();
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .body(content);
    }

    @RequestMapping(method=RequestMethod.GET, path="/view-study/{id:\\d+}/file/{fileId:\\d+}")
    public @ResponseBody ResponseEntity<byte[]> getStudyFile(@PathVariable("fileId") long id){
        File f = fs.findById(id).orElseThrow(() -> new NoSuchElementException("File not found with ID: " + id));
        
        byte[] content = f.getContent();
        FileTypeEnum fileType = f.getType();

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileType.getName()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"file_" + id + fileType.getExtension() + "\"")
                .body(content);
    }
}
