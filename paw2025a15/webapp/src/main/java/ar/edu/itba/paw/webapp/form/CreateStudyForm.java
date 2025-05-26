package ar.edu.itba.paw.webapp.form;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import ar.edu.itba.paw.webapp.form.constraints.ValidStudyFile;

public class CreateStudyForm {

    @NotNull(message = "${uploadStudies.file.errorMessage}")
    @ValidStudyFile
    private MultipartFile file;

    @NotNull(message = "${uploadStudies.type.errorMessage}")
    private StudyTypeEnum type;

    @Size(max = 100, message = "${uploadStudies.comment.errorMessage}")
    private String comment;

    @PastDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private List<Long> AuthDoctorIds;

    public List<Long> getAuthDoctorIds() { return AuthDoctorIds; }

    public void setAuthDoctorIds(List<Long> authDoctorIds) { AuthDoctorIds = authDoctorIds; }

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