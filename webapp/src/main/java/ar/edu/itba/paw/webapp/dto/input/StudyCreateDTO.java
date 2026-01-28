package ar.edu.itba.paw.webapp.dto.input;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidStudyFile;

public class StudyCreateDTO {

    @NotNull
    private StudyTypeEnum type;

    @Size
    private String comment;

    @NotNull
    @ValidStudyFile
    private List<URI> files;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate studyDate;

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

    public List<URI> getFiles(){
        return files;
    }

    public void setFiles(List<URI> files){
        this.files = files;
    }

    public LocalDate getStudyDate(){
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate){
        this.studyDate = studyDate;
    }
    
}
