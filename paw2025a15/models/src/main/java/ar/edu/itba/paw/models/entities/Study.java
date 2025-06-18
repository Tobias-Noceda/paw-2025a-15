package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "studies")
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studies_study_id_seq")
    @SequenceGenerator(sequenceName = "studies_study_id_seq", name = "studies_study_id_seq", allocationSize = 1)
    @Column( name = "study_id")
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column( name = "study_type", nullable = false)
    private StudyTypeEnum type = StudyTypeEnum.fromInt(0);

    @Column( name = "study_comment", length = 100)
    private String comment;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_files",
        joinColumns = @JoinColumn(name = "study_id"),
        inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<File> files;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", referencedColumnName = "user_id", nullable = false)
    private User uploader;

    @Column (name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column( name = "study_date", nullable = false)
    private LocalDate studyDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "auth_studies",
        joinColumns = @JoinColumn(name = "study_id", referencedColumnName = "study_id"),
        inverseJoinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id")
    )
    List<Doctor> authDoctors;

    public Study(){
        //just for hibernate
    }

    public Study(StudyTypeEnum type, String comment, List<File> files, Patient patient, User uploader, LocalDateTime uploadDate, LocalDate studyDate){
        this.type = type;
        this.comment = comment;
        this.files = files;
        this.patient = patient;
        this.uploader = uploader;
        this.uploadDate = uploadDate;
        this.studyDate = studyDate;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
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

    public List<File> getFiles(){
        return files;
    }

    public void setFiles(List<File> files){
        this.files = files;
    }

    public Patient getPatient(){
        return patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public User getUploader(){
        return uploader;
    }

    public void setUploader(User uploader){
        this.uploader = uploader;
    }

    public LocalDateTime getUploadDate(){
        return uploadDate;
    }

    public Date getUploadDateAsDate(){
        return Date.from(uploadDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void setUploadDate(LocalDateTime uploadDate){
        this.uploadDate = uploadDate;
    }

    public LocalDate getStudyDate(){
        return studyDate;
    }

    public Date getStudyDateAsDate(){
        return Date.from(studyDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setStudyDate(LocalDate studyDate){
        this.studyDate = studyDate;
    }

    public List<Doctor> getAuthDoctors() {
        return authDoctors;
    }

    public void setAuthDoctors(List<Doctor> authDoctors) {
        this.authDoctors = authDoctors;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof Study)) return false;

        Study o = (Study) other;

        return (this.id.equals(o.id));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + type.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + files.hashCode();
        result = 31 * result + patient.hashCode();
        result = 31 * result + uploader.hashCode();
        result = 31 * result + uploadDate.hashCode();
        result = 31 * result + studyDate.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "File{" +
            "id=" + id +
            ", type=" + type +
            ", comment=" + comment +
            ", files=" + files +
            ", user=" + patient +
            ", uploader=" + uploader +
            ", uploadDate=" + uploadDate +
            ", studyDate=" + studyDate +
            '}';
    }
}