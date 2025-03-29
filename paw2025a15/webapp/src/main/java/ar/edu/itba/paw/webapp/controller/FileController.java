package ar.edu.itba.paw.webapp.controller;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.File;

@Controller
public class FileController {

    private final FileService fs;

    @Autowired
    public FileController(final FileService fs){
        this.fs = fs;
    }

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.GET)
    public ModelAndView getImageForm(){
        return new ModelAndView("createFile");
    }

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.POST)
    public ModelAndView createImage(@RequestParam("file") MultipartFile file) throws IOException{
        String fileType = file.getContentType();
        if (fileType == null || !(fileType.equals("image/png") || fileType.equals("image/jpeg") || fileType.equals("application/pdf"))) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
        
        byte[] content = file.getBytes();
        File f = fs.create(content, fileType);
        return new ModelAndView("redirect:/supersecret/files/" + f.getId());
    }

    @RequestMapping(method=RequestMethod.GET, path="/supersecret/files/{file_id:\\d+}")
    public @ResponseBody ResponseEntity<byte[]> getImage(@PathVariable("file_id") long id){
        Optional<File> f = fs.findById(id);
        if (!f.isPresent()) {
            throw new NoSuchElementException("File not found with ID: " + id);
        }
        
        byte[] content = f.get().getContent();
        String fileType = f.get().getType();
        
        MediaType mediaType;
        switch (fileType) {
            case "image/png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "image/jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "application/pdf":
                mediaType = MediaType.APPLICATION_PDF;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
        }

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(content);
    }
}
