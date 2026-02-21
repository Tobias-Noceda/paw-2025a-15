package ar.edu.itba.paw.webapp.dto.input;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.webapp.dto.validation.NonEmptyBody;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;

@NonEmptyBody
public class StudyCreateDTO {

    @NotNull
    @NotBlank
    private String type;

    @Size
    @NotNull
    @NotBlank
    private String comment;

    @NotNull
    @Size(min = 1)
    private List<@URL(protocol = "http")String> files;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate studyDate;

    private List<@URL(protocol = "http") String> authorizedDoctors;

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
    
    public List<String> getAuthorizedDoctors() {
        return authorizedDoctors;
    }

    public void setAuthorizedDoctors(List<String> authorizedDoctors) {
        this.authorizedDoctors = authorizedDoctors;
    }
}
