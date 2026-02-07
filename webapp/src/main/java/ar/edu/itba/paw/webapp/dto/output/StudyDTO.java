package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.webapp.controller.DoctorController;
import ar.edu.itba.paw.webapp.controller.FileController;
import ar.edu.itba.paw.webapp.controller.PatientController;

public class StudyDTO {

    private String type;
    private String comment;
    private LocalDateTime uploadDate;
    private LocalDate studyDate;
    
    private LinkDTO links;

    public static Function<Study, StudyDTO> mapper(final UriInfo uriInfo){
        return (s) -> fromStudy(uriInfo, s);
    }

    public static StudyDTO fromStudy(final UriInfo uriInfo, Study study){
        final StudyDTO dto = new StudyDTO();

        dto.setType(study.getType().getdisplayName());
        dto.setComment(study.getComment());
        dto.setUploadDate(study.getUploadDate());
        dto.setStudyDate(study.getStudyDate());

        URI self = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getPatient().getId())).path("studies").path(String.valueOf(study.getId())).build();
        URI patient =  uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getPatient().getId())).build();
        URI uploader = null;
        if(study.getUploader().getRole().equals(UserRoleEnum.DOCTOR))uploader = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(study.getUploader().getId())).build();
        else if(study.getUploader().getRole().equals(UserRoleEnum.PATIENT)) uploader = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getUploader().getId())).build();
        URI files = uriInfo.getBaseUriBuilder().path(FileController.class).queryParam("studyId", study.getId()).build();
        URI authDoctors = uriInfo.getBaseUriBuilder().path(DoctorController.class).queryParam("studyId", study.getId()).build();

        dto.setLinks(new LinkDTO()
            .setSelf(self)
            .setPatient(patient)
            .setUploader(uploader)
            .setAuthDoctors(authDoctors)
            .setFiles(files)
        );

        return dto;
    }

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

    public LocalDateTime getUploadDate(){
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate){
        this.uploadDate = uploadDate;
    }

    public LocalDate getStudyDate(){
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate){
        this.studyDate = studyDate;
    }

    public LinkDTO getLinks(){
        return links;
    }

    public void setLinks(LinkDTO links){
        this.links = links;
    }
}
