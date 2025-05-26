package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "studies")
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studies_study_id_seq")
    @SequenceGenerator(sequenceName = "studies_study_id_seq", name = "studies_study_id_seq", allocationSize = 1)
    @Column( name = "study_id")
    private long id;

    @Enumerated(EnumType.ORDINAL)
    @Column( name = "study_type", nullable = false)
    private StudyTypeEnum type = StudyTypeEnum.fromInt(0);

    @Column( name = "study_comment", length = 100)
    private String comment;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", referencedColumnName = "file_id", nullable = false)
    private File file;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id", referencedColumnName = "user_id", nullable = false)
    private User uploader;

    @Column (name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column( name = "study_date", nullable = false)
    private LocalDate studyDate;

    public Study(){
        //just for hibernate
    }

    public Study(StudyTypeEnum type, String comment, File file, User user, User uploader, LocalDateTime uploadDate, LocalDate studyDate){
        this.type = type;
        this.comment = comment;
        this.file = file;
        this.user = user;
        this.uploader = uploader;
        this.uploadDate = uploadDate;
        this.studyDate = studyDate;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
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

    public File getFile(){
        return file;
    }

    public void setFile(File file){
        this.file = file;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
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

    public void setUploadDate(LocalDateTime uploadDate){
        this.uploadDate = uploadDate;
    }

    public LocalDate getStudyDate(){
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate){
        this.studyDate = studyDate;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof Study)) return false;

        Study o = (Study) other;

        return (this.id==o.id) && (this.type.equals(o.type))
        && (this.comment.equals(o.comment)) && (this.file.equals(o.file))
        && (this.user.equals(o.user)) && (this.uploader.equals(o.uploader))
        && (this.uploadDate.equals(o.uploadDate)) && (this.studyDate.equals(o.studyDate));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + type.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + file.hashCode();
        result = 31 * result + user.hashCode();
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
            ", file=" + file +
            ", user=" + user +
            ", uploader=" + uploader +
            ", uploadDate=" + uploadDate +
            ", studyDate=" + studyDate +
            '}';
    }
}