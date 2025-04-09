package ar.edu.itba.paw.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class createStudyForm {

    @NotNull(message = "A file must be uploaded")
    private MultipartFile file;

    @Size(min = 1, max = 100, message = "Study information should be specified and in less than 100 characters")
    private String type;

    public MultipartFile getFile(){
        return file;
    }

    public void setFile(MultipartFile file){
        this.file = file;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }
}
