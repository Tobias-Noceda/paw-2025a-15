package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.time.LocalDate;
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
    private LocalDate uploadDate;
    private LocalDate studyDate;
    
    private LinkDTO links;

    public static Function<Study, StudyDTO> mapper(final UriInfo uriInfo){
        return (s) -> fromStudy(uriInfo, s);
    }

    public static StudyDTO fromStudy(final UriInfo uriInfo, Study study){
        final StudyDTO dto = new StudyDTO();

        dto.setType(study.getType().getdisplayName());
        dto.setComment(study.getComment());
        dto.setUploadDate(study.getUploadDate().toLocalDate());
        dto.setStudyDate(study.getStudyDate());

        URI baseSelf = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getPatient().getId())).path("studies").path(String.valueOf(study.getId())).build();
        TemplatedLinkDTO self = TemplatedLinkDTO.of(baseSelf);
        URI basePatient =  uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getPatient().getId())).build();
        TemplatedLinkDTO patient = TemplatedLinkDTO.of(basePatient);
        URI baseUploader = null;
        if(study.getUploader().getRole().equals(UserRoleEnum.DOCTOR)) baseUploader = uriInfo.getBaseUriBuilder().path(DoctorController.class).path(String.valueOf(study.getUploader().getId())).build();
        else if(study.getUploader().getRole().equals(UserRoleEnum.PATIENT)) baseUploader = uriInfo.getBaseUriBuilder().path(PatientController.class).path(String.valueOf(study.getUploader().getId())).build();
        TemplatedLinkDTO uploader = TemplatedLinkDTO.of(baseUploader);
        URI baseFiles = uriInfo.getBaseUriBuilder().path(FileController.class).queryParam("studyId", study.getId()).build();
        TemplatedLinkDTO files = TemplatedLinkDTO.of(baseFiles);
        URI baseAuthDoctors = uriInfo.getBaseUriBuilder().path(DoctorController.class).queryParam("studyId", study.getId()).build();
        TemplatedLinkDTO authDoctors = TemplatedLinkDTO.of(baseAuthDoctors);

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

    public LocalDate getUploadDate(){
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate){
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
