package ar.edu.itba.paw.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.StudyTypeEnum;

public class CreateStudyForm {

    @NotNull(message = "${uploadStudies.file.errorMessage}")
    private MultipartFile file;

    @NotNull(message = "Must specify type")//TODO inter!
    private StudyTypeEnum type;

    @Size(min = 1, max = 100, message = "${uploadStudies.type.errorMessage}")
    private String comment;

    public MultipartFile getFile(){
        return file;
    }

    public void setFile(MultipartFile file){
        this.file = file;
    }

    public StudyTypeEnum getType(){
        return type;
    }

    public void setType(StudyTypeEnum type){
        this.type = type;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }
}