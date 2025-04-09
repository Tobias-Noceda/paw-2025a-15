package ar.edu.itba.paw.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class CreateStudyForm {

    @NotNull(message = "${uploadStudies.file.errorMessage}")
    private MultipartFile file;

    @Size(min = 1, max = 100, message = "${uploadStudies.type.errorMessage}")
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