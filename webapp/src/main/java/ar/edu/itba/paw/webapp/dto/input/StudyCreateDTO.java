package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.webapp.form.constraints.PastDate;

public class StudyCreateDTO {

    @NotNull
    @NotBlank
    private String type;

    @Size
    private String comment;

    @NotNull
    //@ValidStudyFile TODO
    @Size(min = 1)
    private List<@URL(protocol = "http")String> files;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate studyDate;

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public List<String> getFiles(){
        return files;
    }

    public void setFiles(List<String> files){
        this.files = files;
    }

    public LocalDate getStudyDate(){
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate){
        this.studyDate = studyDate;
    }
    
}
