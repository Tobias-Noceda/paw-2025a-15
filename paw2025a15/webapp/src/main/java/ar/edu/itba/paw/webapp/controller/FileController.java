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
import ar.edu.itba.paw.models.FileTypeEnum;

@Controller
public class FileController {

    @Autowired
    private FileService fs;

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.GET)
    public ModelAndView getImageForm(){
        return new ModelAndView("createFile");
    }

    @RequestMapping(path = "/supersecret/file", method = RequestMethod.POST)
    public ModelAndView createImage(@RequestParam("file") MultipartFile file) throws IOException{
        File f = fs.create(file.getBytes(), FileTypeEnum.fromString(file.getContentType()));
        return new ModelAndView("redirect:/supersecret/files/" + f.getId());
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
}
