package ar.edu.itba.paw.form;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.form.constraints.PastDate;
import ar.edu.itba.paw.models.StudyTypeEnum;

public class CreateStudyForm {

    @NotNull(message = "${uploadStudies.file.errorMessage}")
    private MultipartFile file;

    @NotNull(message = "${uploadStudies.type.errorMessage}")
    private StudyTypeEnum type;

    @Size(max = 100, message = "${uploadStudies.comment.errorMessage}")
    private String comment;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

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

    public void setDate(LocalDate date){
        this.date = date;
    }

    public LocalDate getDate(){
        return date;
    }
}